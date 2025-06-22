from flask import Flask, jsonify, request
import pandas as pd
import numpy as np
import mysql.connector
from mysql.connector import Error
from mysql.connector.pooling import MySQLConnectionPool
import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry
import torch
from sklearn.preprocessing import MultiLabelBinarizer
from transformers import BertTokenizer, BertModel
import os
import logging
import redis
import pickle
import tensorflow as tf
from tensorflow.keras.models import load_model
from tensorflow.keras.losses import MeanSquaredError
from tensorflow.keras.metrics import CosineSimilarity
import random

app = Flask(__name__)

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Connect to Redis
redis_client = redis.Redis(host=os.getenv("REDIS_HOST", "redis"), port=int(os.getenv("REDIS_PORT", 6379)), decode_responses=False)

# Configure requests with retry
session = requests.Session()
retries = Retry(total=3, backoff_factor=0.5, status_forcelist=[500, 502, 503, 504])
session.mount("https://", HTTPAdapter(max_retries=retries))

# MySQL connection configuration
db_config = {
    'host': os.getenv("MYSQL_HOST", "mysql-db"),
    'port': int(os.getenv("MYSQL_PORT", "3306")),
    'user': os.getenv("MYSQL_USER", "root"),
    'password': os.getenv("MYSQL_PASSWORD", "crystal2001"),
    'database': os.getenv("MYSQL_DATABASE", "cinema_db")
}

# Create MySQL connection pool
try:
    connection_pool = MySQLConnectionPool(pool_name="mypool", pool_size=5, **db_config)
    logger.info("MySQL connection pool created successfully!")
except Error as e:
    logger.error(f"Error creating MySQL connection pool: {e}")
    raise

# TMDB API Key
tmdb_api_key = os.getenv("API_KEY")
if not tmdb_api_key:
    logger.error("API_KEY not set in environment variables!")
    raise ValueError("API_KEY not set!")

# Load Keras model
input_dim = 789  # 19 genres + 768 BERT + 1 vote_average + 1 popularity
try:
    model = load_model("models/keras_lstm_model.h5", custom_objects={
        'MeanSquaredError': MeanSquaredError,
        'CosineSimilarity': CosineSimilarity
    })
    logger.info("Keras LSTM model loaded successfully!")
except Exception as e:
    logger.error(f"Error loading keras_lstm_model.h5: {e}")
    raise

# Define all genres
ALL_GENRES = [
    'Action', 'Adventure', 'Animation', 'Comedy', 'Crime', 'Documentary', 'Drama',
    'Family', 'Fantasy', 'History', 'Horror', 'Music', 'Mystery', 'Romance',
    'Science Fiction', 'Thriller', 'TV Movie', 'War', 'Western'
]

# Load MultiLabelBinarizer
try:
    with open("models/mlb.pkl", "rb") as f:
        mlb = pickle.load(f)
    logger.info(f"MultiLabelBinarizer loaded with {len(mlb.classes_)} genres: {mlb.classes_}")
except Exception as e:
    logger.error(f"Error loading mlb.pkl: {e}")
    raise

# Initialize BERT
try:
    cache_dir = os.getenv("TRANSFORMERS_CACHE", "/models/bert-base-uncased")
    tokenizer = BertTokenizer.from_pretrained("bert-base-uncased", cache_dir=cache_dir)
    bert_model = BertModel.from_pretrained("bert-base-uncased", cache_dir=cache_dir)
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    bert_model.to(device)
    bert_model.eval()
    logger.info("BERT model initialized successfully!")
except Exception as e:
    logger.error(f"Error initializing BERT: {e}")
    bert_model = None

# Keywords and descriptions for overview analysis
MOOD_KEYWORDS = {
    'funny': ['funny', 'comedy', 'laugh', 'hilarious', 'humor', 'joke'],
    'thrilling': ['thrilling', 'intense', 'action', 'exciting', 'suspense', 'adventure'],
    'heartwarming': ['heartwarming', 'romantic', 'gentle', 'calm', 'love', 'family'],
    'emotional': ['emotional', 'moving', 'touching', 'drama', 'tear'],
    'scary': ['horror', 'scary', 'terrifying', 'creepy']
}

MOOD_DESCRIPTIONS = {
    'funny': 'humorous with funny and unexpected situations',
    'thrilling': 'exciting with intense action and suspenseful moments',
    'heartwarming': 'gentle and warm, filled with emotional and heartfelt moments',
    'emotional': 'deep and moving, touching the viewer’s heart',
    'scary': 'terrifying with creepy and tense moments',
    'generic': 'engaging with a diverse and captivating storyline'
}

def get_bert_embedding(text):
    if not isinstance(text, str) or not text or bert_model is None:
        logger.warning("BERT unavailable or text empty, returning zero embedding")
        return np.zeros(768)
    try:
        inputs = tokenizer(text, return_tensors="pt", max_length=128, truncation=True, padding=True)
        inputs = {k: v.to(device) for k, v in inputs.items()}
        with torch.no_grad():
            outputs = bert_model(**inputs)
        return outputs.last_hidden_state[:, 0, :].squeeze().cpu().numpy()  # CLS token
    except Exception as e:
        logger.error(f"Error generating BERT embedding: {e}")
        return np.zeros(768)

def analyze_overview(overview):
    if not isinstance(overview, str) or not overview:
        return {'moods': ['generic'], 'summary': MOOD_DESCRIPTIONS['generic']}
    moods = []
    lower_overview = overview.lower()
    for mood, keywords in MOOD_KEYWORDS.items():
        if any(keyword in lower_overview for keyword in keywords):
            moods.append(mood)
    if not moods:
        moods = ['generic']
    
    if len(moods) == 1:
        summary = f"This movie features a {MOOD_DESCRIPTIONS[moods[0]]} plot."
    elif len(moods) == 2:
        summary = f"This movie combines a {MOOD_DESCRIPTIONS[moods[0]]} plot with {MOOD_DESCRIPTIONS[moods[1]]} elements."
    else:
        summary = f"This movie offers a {MOOD_DESCRIPTIONS[moods[0]]} plot with additional engaging elements."
    
    return {'moods': moods, 'summary': summary}

def get_movie_details(tmdb_id):
    cache_key = f"movie_details:{tmdb_id}"
    try:
        cached = redis_client.get(cache_key)
        if cached:
            logger.info(f"Retrieved movie details for {tmdb_id} from Redis cache")
            return pickle.loads(cached)
    except Exception as e:
        logger.error(f"Error retrieving cache for movie {tmdb_id}: {e}")

    url = f"https://api.themoviedb.org/3/movie/{tmdb_id}?api_key={tmdb_api_key}"
    try:
        response = session.get(url)
        if response.status_code != 200:
            logger.warning(f"Failed to fetch movie {tmdb_id}: {response.status_code}")
            return None
        data = response.json()
        overview_analysis = analyze_overview(data.get('overview', ''))
        details = {
            'title': data.get('title', ''),
            'genres': [g['name'] for g in data.get('genres', []) if g['name'] in ALL_GENRES],
            'overview': data.get('overview', ''),
            'moods': overview_analysis['moods'],
            'overview_summary': overview_analysis['summary'],
            'poster_path': data.get('poster_path', '')  # Thêm poster_path
        }
        try:
            redis_client.setex(cache_key, 86400, pickle.dumps(details))  # Cache for 24h
            logger.info(f"Cached movie details for {tmdb_id} in Redis")
        except Exception as e:
            logger.error(f"Error caching movie details for {tmdb_id}: {e}")
        return details
    except Exception as e:
        logger.error(f"Error fetching movie details for {tmdb_id}: {e}")
        return None

def get_movie_features(tmdb_id):
    cache_key = f"movie_features:{tmdb_id}"
    try:
        cached = redis_client.get(cache_key)
        if cached:
            logger.info(f"Retrieved movie features for {tmdb_id} from Redis cache")
            return pickle.loads(cached)
    except Exception as e:
        logger.error(f"Error retrieving cache for movie {tmdb_id}: {e}")

    url = f"https://api.themoviedb.org/3/movie/{tmdb_id}?api_key={tmdb_api_key}"
    try:
        response = session.get(url)
        if response.status_code != 200:
            logger.warning(f"Failed to fetch movie {tmdb_id}: {response.status_code}")
            return None
        data = response.json()
        genres = [g["name"] for g in data.get("genres", []) if g["name"] in ALL_GENRES]
        logger.info(f"Genres for movie {tmdb_id}: {genres}")
        genres_vec = mlb.transform([genres])[0]
        logger.info(f"Genres vector size for movie {tmdb_id}: {genres_vec.shape}")
        overview = data.get("overview", "")
        overview_vec = get_bert_embedding(overview)
        vote_avg = np.array([data.get("vote_average", 0) / 10.0])
        popularity = np.array([data.get("popularity", 0) / 1000.0])
        features = np.concatenate([genres_vec, overview_vec, vote_avg, popularity])
        logger.info(f"Feature vector size for movie {tmdb_id}: {features.shape}")
        
        try:
            redis_client.setex(cache_key, 86400, pickle.dumps(features))  # Cache for 24h
            logger.info(f"Cached movie features for {tmdb_id} in Redis")
        except Exception as e:
            logger.error(f"Error caching movie features for {tmdb_id}: {e}")
        
        return features
    except Exception as e:
        logger.error(f"Error fetching movie features for {tmdb_id}: {e}")
        return None

def get_booked_movie_ids(user_id):
    try:
        conn = connection_pool.get_connection()
        if not conn.is_connected():
            logger.warning(f"Database connection lost for user_id {user_id}, reconnecting")
            conn.reconnect(attempts=3, delay=1)
        cursor = conn.cursor()
        username_query = "SELECT username FROM users WHERE id = %s"
        cursor.execute(username_query, (user_id,))
        result = cursor.fetchone()
        if not result:
            logger.warning(f"No username found for user_id {user_id}")
            cursor.close()
            conn.close()
            return []
        username = result[0]
        query = "SELECT DISTINCT movie_id FROM bookings WHERE user_id = %s"
        cursor.execute(query, (username,))
        booked_movies = [str(row[0]) for row in cursor.fetchall()]
        cursor.close()
        conn.close()
        logger.info(f"Retrieved {len(booked_movies)} booked movies for user_id {user_id} (username: {username}): {booked_movies}")
        return booked_movies
    except Exception as e:
        logger.error(f"Error fetching booking history for user_id {user_id}: {e}")
        return []
def get_recommendations(user_id, sequence_length=3, n=10):
    logger.debug(f"Starting recommendations for user_id {user_id}")
    booked_movie_ids = get_booked_movie_ids(user_id)
    if not booked_movie_ids:
        logger.warning(f"No booking history found for user_id {user_id}")
        return []

    unique_movie_ids = list(dict.fromkeys(booked_movie_ids))
    booked_movies = []
    genre_counts = {}
    mood_counts = {}
    
    for movie_id in unique_movie_ids:
        details = get_movie_details(movie_id)
        if details:
            booked_movies.append({
                'movieId': movie_id,
                'title': details['title'],
                'genres': details['genres'],
                'moods': details['moods'],
                'overview_summary': details['overview_summary']
            })
            for genre in details.get('genres', []):
                genre_counts[genre] = genre_counts.get(genre, 0) + 1
            for mood in details.get('moods', []):
                mood_counts[mood] = mood_counts.get(mood, 0) + 1

    top_genres = sorted(genre_counts.items(), key=lambda x: x[1], reverse=True)[:2]
    top_moods = sorted(mood_counts.items(), key=lambda x: x[1], reverse=True)[:2]
    top_genres_names = [g[0] for g in top_genres]
    top_moods_names = [m[0] for m in top_moods]

    example_movies = [m['title'] for m in booked_movies[:2]]
    example_movie = example_movies[0] if example_movies else "your previous movies"

    # Định nghĩa các mẫu câu tự nhiên
    reason_templates = [
        # Cả thể loại và cảm xúc
        lambda g, m: f"You loved the {', '.join(g).lower()} in {example_movie}, so you'll vibe with this {m[0]} story!",
        lambda g, m: f"Since you enjoyed {', '.join(g).lower()} like in {example_movie}, this {m[0]} flick is perfect for you.",
        lambda g, m: f"This {m[0]} movie has the {', '.join(g).lower()} you liked in {example_movie}—check it out!",
        # Chỉ thể loại
        lambda g, m: f"You seem to dig {', '.join(g).lower()} like in {example_movie}, so this one's right up your alley.",
        lambda g, m: f"With {', '.join(g).lower()} vibes similar to {example_movie}, this movie's a great pick for you!",
        # Chỉ cảm xúc
        lambda g, m: f"This {m[0]} tale feels like the kind of story you'd enjoy after {example_movie}.",
        lambda g, m: f"Looking for something {m[0]}? This movie's got the same charm as {example_movie}.",
        # Chung chung
        lambda g, m: f"This movie feels like a great match for your taste, inspired by {example_movie}.",
        lambda g, m: f"Based on your love for {example_movie}, this one's sure to hit the spot!"
    ]

    def generate_reason(matching_genres, matching_moods):
        if matching_genres and matching_moods:
            templates = reason_templates[:3]  # Cả thể loại và cảm xúc
        elif matching_genres:
            templates = reason_templates[3:5]  # Chỉ thể loại
        elif matching_moods:
            templates = reason_templates[5:7]  # Chỉ cảm xúc
        else:
            templates = reason_templates[7:]  # Chung chung
        
        # Chọn ngẫu nhiên một mẫu
        template = random.choice(templates)
        # Format thể loại và cảm xúc
        genres_str = [g.lower() for g in matching_genres] if matching_genres else []
        moods_str = [MOOD_DESCRIPTIONS.get(m, m).split(' with ')[0].lower() for m in matching_moods] if matching_moods else []
        return template(genres_str, moods_str)

    is_content_based = len(unique_movie_ids) >= sequence_length
    logger.debug(f"Content-based: {is_content_based}, booked movies: {len(unique_movie_ids)}")

    if not is_content_based:
        movies = []
        for movie_id in unique_movie_ids:
            url = f"https://api.themoviedb.org/3/movie/{movie_id}?api_key={tmdb_api_key}"
            try:
                response = session.get(url)
                if response.status_code == 200:
                    data = response.json()
                    movies.append({
                        "movie_id": str(data["id"]),
                        "genres": [g["name"] for g in data.get("genres", []) if g["name"] in ALL_GENRES]
                    })
            except Exception as e:
                logger.error(f"Error fetching movie {movie_id}: {e}")
        if not movies:
            logger.warning(f"No valid movies fetched for user_id {user_id}")
            return []
        movies_df = pd.DataFrame(movies)
        genres_encoded = mlb.transform(movies_df["genres"])
        genres_df = pd.DataFrame(genres_encoded, columns=mlb.classes_, index=movies_df["movie_id"])
        user_profile = genres_df.mean().values

        url = f"https://api.themoviedb.org/3/discover/movie?api_key={tmdb_api_key}&sort_by=popularity.desc"
        try:
            response = session.get(url)
            if response.status_code != 200:
                logger.warning(f"Error calling TMDB discover API: {response.status_code}")
                return []
            all_movies = response.json().get("results", [])
        except Exception as e:
            logger.error(f"Error fetching movie list from TMDB: {response}")
            return []

        recommendations = []
        for movie in all_movies:
            movie_id = str(movie["id"])
            if movie_id not in booked_movie_ids:
                details = get_movie_details(movie_id)
                if details:
                    genres_vec = mlb.transform([details['genres']])[0]
                    sim_score = np.dot(user_profile, genres_vec) / (
                        np.linalg.norm(user_profile) * np.linalg.norm(genres_vec) + 1e-10
                    )
                    movie_genres = details['genres']
                    movie_moods = details['moods']
                    matching_genres = [g for g in movie_genres if g in top_genres_names]
                    matching_moods = [m for m in movie_moods if m in top_moods_names]
                    reason = generate_reason(matching_genres, matching_moods)  # Sử dụng hàm mới
                    recommendations.append((movie_id, sim_score, details['overview_summary'], reason))
        recommendations.sort(key=lambda x: x[1], reverse=True)
        rec_movie_ids = [(movie_id, overview_summary, reason) for movie_id, _, overview_summary, reason in recommendations[:n]]
    else:
        movie_features = []
        for tmdb_id in unique_movie_ids:
            feat = get_movie_features(tmdb_id)
            if feat is None or feat.shape[0] != input_dim:
                logger.warning(f"Invalid features for movie {tmdb_id}: {feat}")
                return []
            movie_features.append((tmdb_id, feat))

        if len(movie_features) < sequence_length:
            logger.warning(f"Insufficient valid movies for sequence: {len(movie_features)}")
            return []

        feature_matrix = np.array([feat for _, feat in movie_features])
        similarity_matrix = np.dot(feature_matrix, feature_matrix.T) / (
            np.linalg.norm(feature_matrix, axis=1)[:, None] * np.linalg.norm(feature_matrix, axis=1)[None, :] + 1e-10
        )
        start_movie_idx = 0
        similar_indices = np.argsort(similarity_matrix[start_movie_idx])[-sequence_length:][::-1]
        seq = [movie_features[idx][0] for idx in similar_indices[:sequence_length]]
        seq_features = [movie_features[idx][1] for idx in similar_indices[:sequence_length]]

        seq_features = np.array(seq_features)
        logger.info(f"Sequence features shape: {seq_features.shape}")
        if seq_features.shape != (sequence_length, input_dim):
            logger.error(f"Invalid sequence features shape: {seq_features.shape}, expected ({sequence_length}, {input_dim})")
            return []
        seq_array = seq_features.reshape(1, sequence_length, input_dim)
        logger.info(f"Reshaped sequence array shape: {seq_array.shape}")

        try:
            pred_vector = model.predict(seq_array, verbose=0)[0]
            logger.info(f"Predicted vector shape: {pred_vector.shape}")
            if pred_vector.shape[0] != input_dim:
                logger.error(f"Invalid prediction shape: {pred_vector.shape}, expected ({input_dim},)")
                return []
        except Exception as e:
            logger.error(f"Error predicting with Keras LSTM: {e}")
            return []

        url = f"https://api.themoviedb.org/3/discover/movie?api_key={tmdb_api_key}&sort_by=popularity.desc"
        try:
            response = session.get(url)
            if response.status_code != 200:
                logger.warning(f"Error calling TMDB discover API: {response.status_code}")
                return []
            all_movies = response.json().get("results", [])
        except Exception as e:
            logger.error(f"Error fetching movie list from TMDB: {e}")
            return []

        recommendations = []
        for movie in all_movies:
            movie_id = str(movie["id"])
            if movie_id not in booked_movie_ids:
                feat = get_movie_features(movie_id)
                details = get_movie_details(movie_id)
                if feat is not None and details is not None:
                    sim_score = np.dot(pred_vector, feat) / (
                        np.linalg.norm(pred_vector) * np.linalg.norm(feat) + 1e-10
                    )
                    movie_genres = details['genres']
                    movie_moods = details['moods']
                    matching_genres = [g for g in movie_genres if g in top_genres_names]
                    matching_moods = [m for m in movie_moods if m in top_moods_names]
                    reason = generate_reason(matching_genres, matching_moods)  # Sử dụng hàm mới
                    recommendations.append((movie_id, sim_score, details['overview_summary'], reason))
        recommendations.sort(key=lambda x: x[1], reverse=True)
        rec_movie_ids = [(movie_id, overview_summary, reason) for movie_id, _, overview_summary, reason in recommendations[:n]]

    movies = []
    for movie_id, overview_summary, reason in rec_movie_ids:
        try:
            response = session.get(f"https://api.themoviedb.org/3/movie/{movie_id}?api_key={tmdb_api_key}")
            if response.status_code == 200:
                data = response.json()
                movies.append({
                    "movieId": str(data["id"]),
                    "title": data.get("title", ""),
                    "genres": [g["name"] for g in data.get("genres", []) if g["name"] in ALL_GENRES],
                    "overview": data.get("overview", ""),
                    "overview_summary": overview_summary,
                    "reason": reason,
                    "posterPath": data.get("poster_path", "")
                })
            else:
                logger.warning(f"Failed to fetch movie {movie_id}: {response.status_code}")
        except Exception as e:
            logger.error(f"Error fetching movie {movie_id}: {e}")

    logger.debug(f"Returning {len(movies)} recommendations for user {user_id}")
    return movies

@app.route("/recommendations", methods=["GET"])
def recommendations():
    try:
        raw_user_id = request.args.get("user_id")
        logger.info(f"Received user_id: {raw_user_id}")
        if raw_user_id is None or raw_user_id.strip() == "":
            logger.warning("user_id is missing or empty")
            return jsonify({"error": "user_id is required"}), 400
        try:
            user_id = int(raw_user_id)
            logger.info(f"Converted user_id: {user_id}")
        except ValueError:
            logger.warning(f"Invalid user_id format: {raw_user_id}")
            return jsonify({"error": "user_id must be an integer"}), 400
        if user_id <= 0:
            logger.warning(f"Invalid user_id value: {user_id}")
            return jsonify({"error": "user_id must be a positive integer"}), 400
        recs = get_recommendations(user_id)
        logger.debug(f"Returning {len(recs)} recommendations for user_id {user_id}")
        return jsonify(recs)
    except Exception as e:
        logger.error(f"Error processing recommendation request: {e}")
        return jsonify({"error": "Internal server error"}), 500

@app.route("/cache/flush", methods=["POST"])
def flush_cache():
    try:
        redis_client.flushdb()
        logger.info("Cleared all Redis cache")
        return jsonify({"status": "Cache flushed successfully"})
    except Exception as e:
        logger.error(f"Error flushing Redis cache: {e}")
        return jsonify({"error": "Failed to flush cache"}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
