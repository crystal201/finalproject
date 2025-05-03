from flask import Flask, jsonify, request
import pandas as pd
import numpy as np
import torch
import torch.nn as nn
import mysql.connector
from mysql.connector import Error
from mysql.connector.pooling import MySQLConnectionPool
import requests
from sklearn.preprocessing import MultiLabelBinarizer
from transformers import BertTokenizer, BertModel
import os

app = Flask(__name__)

# Cấu hình kết nối MySQL từ biến môi trường
db_config = {
    'host': os.getenv("MYSQL_HOST", "mysql-db"),
    'port': int(os.getenv("MYSQL_PORT", "3306")),
    'user': os.getenv("MYSQL_USER", "root"),
    'password': os.getenv("MYSQL_PASSWORD", "crystal2001"),
    'database': os.getenv("MYSQL_DATABASE", "cinema_db")
}

# Tạo connection pool cho MySQL
try:
    connection_pool = MySQLConnectionPool(pool_name="mypool", pool_size=5, **db_config)
    print("Tạo connection pool MySQL thành công!")
except Error as e:
    print(f"Lỗi khi tạo connection pool MySQL: {e}")

# TMDB API Key từ biến môi trường
tmdb_api_key = os.getenv("TMDB_API_KEY")
if not tmdb_api_key:
    raise ValueError("TMDB_API_KEY không được thiết lập trong biến môi trường!")

class MovieLSTM(nn.Module):
    def __init__(self, input_dim, hidden_dim, output_dim, num_layers=2):
        super(MovieLSTM, self).__init__()
        self.lstm = nn.LSTM(input_dim, hidden_dim, num_layers, batch_first=True)
        self.fc = nn.Linear(hidden_dim, output_dim)

    def forward(self, x):
        _, (hidden, _) = self.lstm(x)
        out = self.fc(hidden[-1])
        return out

input_dim = 787
hidden_dim = 256
output_dim = input_dim
model = MovieLSTM(input_dim, hidden_dim, output_dim)
model.load_state_dict(torch.load("models/lstm_model.pth"))
model.eval()

genres_classes = pd.read_csv("models/genres_classes.csv")["0"].tolist()
mlb = MultiLabelBinarizer(classes=genres_classes)
mlb.fit([genres_classes])

tokenizer = BertTokenizer.from_pretrained("bert-base-uncased")
bert_model = BertModel.from_pretrained("bert-base-uncased")

def get_bert_embedding(text):
    if not isinstance(text, str) or not text:
        return np.zeros(768)
    inputs = tokenizer(text, return_tensors="pt", max_length=128, truncation=True, padding=True)
    with torch.no_grad():
        outputs = bert_model(**inputs)
    return outputs.last_hidden_state.mean(dim=1).squeeze().numpy()

def get_movie_features(tmdb_id):
    url = f"https://api.themoviedb.org/3/movie/{tmdb_id}?api_key={tmdb_api_key}"
    response = requests.get(url)
    if response.status_code != 200:
        return None
    data = response.json()
    genres = [g["name"] for g in data["genres"]]
    overview = data["overview"] or ""
    genres_vec = mlb.transform([genres])[0]
    overview_vec = get_bert_embedding(overview)
    return np.concatenate([genres_vec, overview_vec])

def get_booked_movie_ids(user_id):
    conn = connection_pool.get_connection()
    cursor = conn.cursor()
    cursor.execute("""
        SELECT b.movie_id
        FROM bookings b
        JOIN user u ON b.user_id = u.username
        WHERE u.id = %s
        ORDER BY b.created_at
    """, (user_id,))
    booked_movie_ids = [str(row[0]) for row in cursor.fetchall()]
    cursor.close()
    conn.close()
    return booked_movie_ids

def get_recommendations(user_id, sequence_length=3, n=10):
    booked_movie_ids = get_booked_movie_ids(user_id)
    if not booked_movie_ids:
        return []

    unique_movie_ids = list(dict.fromkeys(booked_movie_ids))
    if len(unique_movie_ids) < sequence_length:
        movies = []
        for movie_id in unique_movie_ids:
            url = f"https://api.themoviedb.org/3/movie/{movie_id}?api_key={tmdb_api_key}"
            response = requests.get(url)
            if response.status_code == 200:
                data = response.json()
                movies.append({
                    "movie_id": str(data["id"]),
                    "genres": [g["name"] for g in data["genres"]]
                })
        if not movies:
            return []
        movies_df = pd.DataFrame(movies)
        genres_encoded = mlb.fit_transform(movies_df["genres"])
        genres_df = pd.DataFrame(genres_encoded, columns=mlb.classes_, index=movies_df["movie_id"])
        user_profile = genres_df.mean(axis=0).values

        url = f"https://api.themoviedb.org/3/discover/movie?api_key={tmdb_api_key}&sort_by=popularity.desc"
        response = requests.get(url)
        if response.status_code != 200:
            return []
        all_movies = response.json()["results"]

        recommendations = []
        for movie in all_movies:
            movie_id = str(movie["id"])
            if movie_id not in booked_movie_ids:
                genres = [g["name"] for g in movie["genres"]]
                genres_vec = mlb.transform([genres])[0]
                sim = np.dot(user_profile, genres_vec) / (
                    np.linalg.norm(user_profile) * np.linalg.norm(genres_vec) + 1e-9
                )
                recommendations.append((movie_id, sim))
        recommendations.sort(key=lambda x: x[1], reverse=True)
        rec_movie_ids = [movie_id for movie_id, _ in recommendations[:n]]
    else:
        movie_features = []
        for tmdb_id in unique_movie_ids:
            feat = get_movie_features(tmdb_id)
            if feat is None:
                return []
            movie_features.append((tmdb_id, feat))

        feature_matrix = np.array([feat for _, feat in movie_features])
        similarity_matrix = np.dot(feature_matrix, feature_matrix.T) / (
            np.linalg.norm(feature_matrix, axis=1)[:, None] * np.linalg.norm(feature_matrix, axis=1)[None, :] + 1e-9
        )
        start_movie_idx = 0
        similar_indices = np.argsort(similarity_matrix[start_movie_idx])[-sequence_length:][::-1]
        seq = [movie_features[idx][0] for idx in similar_indices[:sequence_length]]
        seq_features = [movie_features[idx][1] for idx in similar_indices[:sequence_length]]

        seq_features = np.array(seq_features)
        seq_tensor = torch.tensor(seq_features, dtype=torch.float32).unsqueeze(0)

        with torch.no_grad():
            pred_vector = model(seq_tensor).squeeze().numpy()

        url = f"https://api.themoviedb.org/3/discover/movie?api_key={tmdb_api_key}&sort_by=popularity.desc"
        response = requests.get(url)
        if response.status_code != 200:
            return []
        all_movies = response.json()["results"]

        recommendations = []
        for movie in all_movies:
            tmdb_id = str(movie["id"])
            if tmdb_id not in booked_movie_ids:
                feat = get_movie_features(tmdb_id)
                if feat is not None:
                    sim = np.dot(pred_vector, feat) / (
                        np.linalg.norm(pred_vector) * np.linalg.norm(feat) + 1e-9
                    )
                    recommendations.append((tmdb_id, sim))
        recommendations.sort(key=lambda x: x[1], reverse=True)
        rec_movie_ids = [movie_id for movie_id, _ in recommendations[:n]]

    movies = []
    for movie_id in rec_movie_ids:
        response = requests.get(f"https://api.themoviedb.org/3/movie/{movie_id}?api_key={tmdb_api_key}")
        if response.status_code == 200:
            data = response.json()
            movies.append({
                "movieId": data["id"],
                "title": data["title"],
                "posterPath": data["poster_path"],
                "genres": ",".join([g["name"] for g in data["genres"]])
            })
    return movies

@app.route("/recommendations", methods=["GET"])
def recommendations():
    user_id = int(request.args.get("user_id"))  # Nhận user_id dạng số
    recs = get_recommendations(user_id)
    return jsonify(recs)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)