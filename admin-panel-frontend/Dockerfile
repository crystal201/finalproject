# Build stage
FROM node:14 as builder

# Tạo user node thay vì dùng root
RUN mkdir -p /home/node/app && chown -R node:node /home/node/app
WORKDIR /home/node/app
USER node

COPY --chown=node:node package*.json ./
RUN npm install

COPY --chown=node:node . .
ARG VUE_APP_API_URL=/api
ENV VUE_APP_API_URL=$VUE_APP_API_URL

# Sửa quyền cho node_modules
RUN chown -R node:node node_modules

RUN npm run build

# Production stage
FROM nginx:1.19-alpine
COPY --from=builder /home/node/app/dist /usr/share/nginx/html
COPY --from=builder /home/node/app/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]