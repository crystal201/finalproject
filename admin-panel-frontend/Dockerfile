# Build stage
FROM node:14 as builder

# Tạo và chuyển sang user node
RUN useradd -m appuser && mkdir -p /app && chown -R appuser:appuser /app
WORKDIR /app
USER appuser

# Copy package files và cài đặt
COPY --chown=appuser:appuser package*.json ./
RUN npm install

# Copy source code
COPY --chown=appuser:appuser . .

# Cài đặt global vue-cli-service và cấp quyền
RUN npm install -g @vue/cli-service
RUN chmod 755 /app/node_modules/.bin/vue-cli-service
RUN chmod -R 755 /app/node_modules

# Build ứng dụng
ARG VUE_APP_API_URL=/api
ENV VUE_APP_API_URL=$VUE_APP_API_URL
RUN npm run build

# Production stage
FROM nginx:1.19-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY --from=builder /app/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]