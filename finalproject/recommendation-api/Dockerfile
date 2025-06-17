FROM python:3.9-slim

WORKDIR /app

# Copy requirements first to leverage caching
COPY requirements.txt .

# Install dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    && rm -rf /var/lib/apt/lists/* \
    && pip install --upgrade pip \
    # Install torch separately with PyTorch index
    && pip install --no-cache-dir torch==2.3.1+cpu -f https://download.pytorch.org/whl/torch_stable.html \
    # Install remaining requirements
    && pip install --no-cache-dir -r requirements.txt --default-timeout=300 --retries=10

# Pre-download BERT model
RUN python -c "from transformers import BertTokenizer, BertModel; \
    tokenizer = BertTokenizer.from_pretrained('bert-base-uncased'); \
    model = BertModel.from_pretrained('bert-base-uncased')"

# Copy project files
COPY . .

# Expose Flask port
EXPOSE 5000

# Run the application
CMD ["python", "recommendation-api.py"]