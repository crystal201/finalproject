# Stage 1: Build
FROM node:14 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install --save-dev cross-env && npm list cross-env
COPY . .
ARG VUE_APP_API_URL=http://157.66.219.181:8081
ENV VUE_APP_API_URL=$VUE_APP_API_URL
RUN chmod +x node_modules/.bin/vue-cli-service
RUN cross-env VUE_APP_API_URL=$VUE_APP_API_URL npm run build

# Stage 2: Run with Node.js and serve
FROM node:14
WORKDIR /app
COPY --from=build /app/dist ./dist
RUN npm install -g serve
ENV NODE_ENV=production
ENV VUE_APP_API_URL=http://157.66.219.181:8081
EXPOSE 80
CMD ["serve", "-s", "dist", "-l", "80"]