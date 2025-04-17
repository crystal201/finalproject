FROM node:16-alpine
RUN apk add --no-cache --virtual .build-deps python3 make g++ git
RUN yarn config set registry https://registry.npmmirror.com && yarn config set network-timeout 600000 -g
WORKDIR /app
COPY --chown=node:node package.json yarn.lock ./
RUN yarn install --frozen-lockfile --network-timeout 600000
COPY --chown=node:node . .
RUN apk del .build-deps && chown -R node:node /app
USER node
ENV HOST=0.0.0.0 PORT=3000
EXPOSE 3000
CMD ["yarn", "dev"]