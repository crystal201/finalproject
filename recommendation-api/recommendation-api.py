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
import logging

app = Flask(__name__)

  # Cấu hình logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

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
    logger.info("Tạo connection pool MySQL thành công!")
except Error as e:
    logger.error(f"Lỗi khi tạo connection pool MySQL: {e}")
    raise

# TMDB API Key từ biến môi trường
tmdb_api_key = os.getenv("TMDB_API_KEY")
if not tmdb_api_key:
    logger.error("TMDB_API_KEY không được thiết lập trong biến môi trường!")
    raise ValueError("TMDB_API_KEY không được thiết lập!")

class MovieLSTM(nn.Module):
    def __init__(self, input_dim, hidden_dim, output_dim, num_layers=2, dropout=0.3):
        super(MovieLSTM, self).__init__()
        self.lstm = nn.LSTM(input_dim, hidden_dim, num_layers, batch_first=True, dropout=dropout)
        self.fc = nn.Linear(hidden_dim, output_dim)

    def forward(self, x):
        _, (hidden, _) = self.lstm(x)
        out = self.fc(hidden[-1])
        return out

# Khởi tạo mô hình
input_dim = 787  # 19 genres + 768 BERT
hidden_dim = 512
output_dim = input_dim
try:
    model = MovieLSTM(input_dim, hidden_dim, output_dim)
    model.load_state_dict(torch.load("models/lstm_model.pth", map_location=torch.device('cpu')))
    model.eval()
    logger.info("Tải mô hình LSTM thành công!")
except Exception as e:
    logger.error(f"Lỗi khi tải lstm_model.pth: {e}")
    raise

# Khởi tạo MultiLabelBinarizer
try:
    genres_classes = pd.read_csv("models/genres_classes.csv")["0"].tolist()
    if len(genres_classes) != 19:
        logger.warning(f"Số genres ({len(genres_classes)}) không khớp với input_dim (19)!")
    mlb = MultiLabelBinarizer(classes=genres_classes)
    mlb.fit([genres_classes])
    logger.info(f"Khởi tạo MultiLabelBinarizer với {len(genres_classes)} genres")
except Exception as e:
    logger.error(f"Lỗi khi đọc genres_classes.csv: {e}")
    raise

# Khởi tạo BERT
try:
    tokenizer = BertTokenizer.from_pretrained("bert-base-uncased")
    bert_model = BertModel.from_pretrained("bert-base-uncased")
    bert_model.eval()
    logger.info("Khởi tạo BERT thành công!")
except Exception as e:
    logger.error(f"Lỗi khi khởi tạo BERT: {e}")
    raise

def get_bert_embedding(text):
    if not isinstance(text, str) or not text:
        return np.zeros(768)
    try:
        inputs = tokenizer(text, return_tensors="pt", max_length=128, truncation=True, padding=True)
        with torch.no_grad():
            outputs = bert_model(**inputs)
        return outputs.last_hidden_state.mean(dim=1).squeeze().numpy()
    except Exception as e:
        logger.error(f"Lỗi khi tạo BERT embedding: {e}")
        return np.zeros(768)

def get_movie_features(tmdb_id):
    url = f"https://api.themoviedb.org/3/movie/{tmdb_id}?api_key={tmdb_api_key}"
    try:
        response = requests.get(url)
        if response.status_code != 200:
            logger.warning(f"Không lấy được thông tin phim {tmdb_id}: {response.status_code}")
            return None
        data = response.json()
        genres = [g["name"] for g in data.get("genres", [])]
        overview = data.get("overview", "")
        genres_vec = mlb.transform([genres])[0]
        overview_vec = get_bert_embedding(overview)
        return np.concatenate([genres_vec, overview_vec])
    except Exception as e:
        logger.error(f"Lỗi khi lấy đặc trưng phim {tmdb_id}: {e}")
        return None

def get_booked_movie_ids(user_id):
    try:
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
        logger.info(f"Lấy được {len(booked_movie_ids)} phim đã đặt cho user_id {user_id}")
        return booked_movie_ids
    except Exception as e:
        logger.error(f"Lỗi khi lấy lịch sử đặt vé cho user_id {user_id}: {e}")
        return []

def get_recommendations(user_id, sequence_length=3, n=10):
    booked_movie_ids = get_booked_movie_ids(user_id)
    if not booked_movie_ids:
        logger.warning(f"Không tìm thấy lịch sử đặt vé cho user_id {user_id}")
        return []

    unique_movie_ids = list(dict.fromkeys(booked_movie_ids))
    if len(unique_movie_ids) < sequence_length:
        movies = []
        for movie_id in unique_movie_ids:
            url = f"https://api.themoviedb.org/3/movie/{movie_id}?api_key={tmdb_api_key}"
            try:
                response = requests.get(url)
                if response.status_code == 200:
                    data = response.json()
                    movies.append({
                        "movie_id": str(data["id"]),
                        "genres": [g["name"] for g in data.get("genres", [])]
                    })
            except Exception as e:
                logger.error(f"Lỗi khi lấy thông tin phim {movie_id}: {e}")
        if not movies:
            return []
        movies_df = pd.DataFrame(movies)
        genres_encoded = mlb.fit_transform(movies_df["genres"])
        genres_df = pd.DataFrame(genres_encoded, columns=mlb.classes_, index=movies_df["movie_id"])
        user_profile = genres_df.mean(axis=0).values

        url = f"https://api.themoviedb.org/3/discover/movie?api_key={tmdb_api_key}&sort_by=popularity.desc"
        try:
            response = requests.get(url)
            if response.status_code != 200:
                logger.warning(f"Lỗi khi gọi TMDB discover API: {response.status_code}")
                return []
            all_movies = response.json().get("results", [])
        except Exception as e:
            logger.error(f"Lỗi khi lấy danh sách phim từ TMDB: {e}")
            return []

        recommendations = []
        for movie in all_movies:
            movie_id = str(movie["id"])
            if movie_id not in booked_movie_ids:
                genres = [g["name"] for g in movie.get("genres", [])]
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
                logger.warning(f"Không lấy được đặc trưng cho phim {tmdb_id}")
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

        try:
            with torch.no_grad():
                pred_vector = model(seq_tensor).squeeze().numpy()
        except Exception as e:
            logger.error(f"Lỗi khi dự đoán với LSTM: {e}")
            return []

        url = f"https://api.themoviedb.org/3/discover/movie?api_key={tmdb_api_key}&sort_by=popularity.desc"
        try:
            response = requests.get(url)
            if response.status_code != 200:
                logger.warning(f"Lỗi khi gọi TMDB discover API: {response.status_code}")
                return []
            all_movies = response.json().get("results", [])
        except Exception as e:
            logger.error(f"Lỗi khi lấy danh sách phim từ TMDB: {e}")
            return []

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
        try:
            response = requests.get(f"https://api.themoviedb.org/3/movie/{movie_id}?api_key={tmdb_api_key}")
            if response.status_code == 200:
                data = response.json()
                movies.append({
                    "movieId": data["id"],
                    "title": data["title"],
                    "posterPath": data["poster_path"],
                    "genres": ",".join([g["name"] for g in data.get("genres", [])])
                })
        except Exception as e:
            logger.error(f"Lỗi khi lấy thông tin phim {movie_id}: {e}")
    return movies

@app.route("/recommendations", methods=["GET"])
def recommendations():
    try:
        user_id = int(request.args.get("user_id"))
        if user_id <= 0:
            logger.warning("user_id không hợp lệ")
            return jsonify({"error": "user_id phải là số nguyên dương"}), 400
        recs = get_recommendations(user_id)
        logger.info(f"Trả về {len(recs)} gợi ý cho user_id {user_id}")
        return jsonify(recs)
    except ValueError:
        logger.warning("user_id không phải số nguyên")
        return jsonify({"error": "user_id phải là số nguyên"}), 400
    except Exception as e:
        logger.error(f"Lỗi khi xử lý yêu cầu gợi ý: {e}")
        return jsonify({"error": "Lỗi server nội bộ"}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)