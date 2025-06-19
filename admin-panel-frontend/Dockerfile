# Build stage
FROM node:14 as builder

# 1. Không tạo user appuser nữa (dùng root để tránh lỗi permission)
WORKDIR /app

# 2. Copy package files trước
COPY package*.json ./

# 3. Cài đặt local @vue/cli-service (không dùng -g)
RUN npm install --save-dev @vue/cli-service
RUN npm install

# 4. Copy source code
COPY . .

# 5. Build với npx để tránh lỗi permission
ARG VUE_APP_API_URL=/api
ENV VUE_APP_API_URL=$VUE_APP_API_URL
RUN npx vue-cli-service build

# Production stage
FROM nginx:1.19-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY --from=builder /app/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]