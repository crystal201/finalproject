# Build stage
FROM node:14 as builder

WORKDIR /app

# 1. Copy package files trước
COPY package*.json ./

# 2. Cài đặt vue-cli-service chính xác
RUN npm install -g @vue/cli
RUN npm install --save-dev @vue/cli-service
RUN npm install

# 3. Copy source code
COPY . .

# 4. Build với đường dẫn đầy đủ
ARG VUE_APP_API_URL=/api
ENV VUE_APP_API_URL=$VUE_APP_API_URL
RUN ./node_modules/.bin/vue-cli-service build

# Production stage
FROM nginx:1.19-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY --from=builder /app/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]