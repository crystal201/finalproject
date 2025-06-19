# Build stage
FROM node:14 as builder

# Tạo và chuyển sang user appuser
RUN useradd -m appuser && mkdir -p /app && chown -R appuser:appuser /app
WORKDIR /app
USER appuser

# Copy package files
COPY --chown=appuser:appuser package*.json ./

# Cài đặt local thay vì global
RUN npm install --save-dev @vue/cli-service
RUN npm install

# Copy source code
COPY --chown=appuser:appuser . .

# Cấp quyền thực thi
RUN chmod 755 node_modules/.bin/*

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