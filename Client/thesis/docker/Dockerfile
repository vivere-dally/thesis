FROM node:12.21-alpine

RUN mkdir -p /home/app

COPY ./build /home/app

WORKDIR /home

RUN npm install -g serve

CMD ["serve", "-s", "app", "-l", "5000"]
