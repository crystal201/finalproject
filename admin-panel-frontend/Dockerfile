# Build stage
FROM node:16-alpine AS builder

# Sử dụng root để build
USER root

WORKDIR /app

# Copy package files trước
COPY package*.json ./

# Cài đặt dependencies
RUN npm install

# Copy source code
COPY . .

# Cấp quyền thực thi
RUN chmod +x node_modules/.bin/vue-cli-service

# Build ứng dụng
ARG VUE_APP_API_URL=/api
ENV VUE_APP_API_URL=$VUE_APP_API_URL
RUN ./node_modules/.bin/vue-cli-service build

# Production stage
FROM nginx:1.19-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY --from=builder /app/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]