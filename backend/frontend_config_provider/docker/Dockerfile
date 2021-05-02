FROM node:12.21-alpine

ENV NODE_ENV=production

RUN mkdir -p /home/app

COPY ./build /home/app

COPY ./package*.json /home/app

WORKDIR /home/app

RUN npm ci --only=production

CMD ["node", "./main.js"]
