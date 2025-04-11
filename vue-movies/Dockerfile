FROM node:16-alpine

# Cài đặt dependencies build
RUN apk add --no-cache --virtual .build-deps \
    python3 \
    make \
    g++ \
    git

# Cấu hình registry và cache
RUN yarn config set registry https://registry.npmmirror.com && \
    yarn config set network-timeout 600000 -g

WORKDIR /app

# Copy package files
COPY --chown=node:node package.json yarn.lock ./

# Cài đặt dependencies
RUN yarn install --frozen-lockfile --network-timeout 600000

# Copy source code
COPY --chown=node:node . .

# Clean up
RUN apk del .build-deps && \
    chown -R node:node /app

USER node

ENV HOST=0.0.0.0 PORT=3000
EXPOSE 3000
CMD ["yarn", "dev"]