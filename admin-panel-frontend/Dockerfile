# Build stage
FROM node:14 as builder

# 1. Sử dụng root để build (tạm thời)
USER root

# 2. Tạo thư mục làm việc
WORKDIR /app

# 3. Copy package files trước để tận dụng cache Docker
COPY package*.json ./

# 4. Cài đặt local @vue/cli-service (KHÔNG dùng -g)
RUN npm install --save-dev @vue/cli-service @vue/cli
RUN npm install

# 5. Copy toàn bộ source code
COPY . .

# 6. Build ứng dụng bằng npx
ARG VUE_APP_API_URL=/api
ENV VUE_APP_API_URL=$VUE_APP_API_URL
RUN npx vue-cli-service build

# Production stage
FROM nginx:1.19-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY --from=builder /app/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]