FROM python:3.9-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
RUN rm -rf /root/.cache/huggingface

COPY . .

EXPOSE 5000
CMD ["python", "recommendation-api.py"]