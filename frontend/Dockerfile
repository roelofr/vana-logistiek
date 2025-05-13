FROM node:lts-slim

COPY dist/frontend /opt/app

EXPOSE 80
ENV ANGULAR_PORT=80 \
    NODE_ENV=production

CMD ["node", "/opt/app/server/server.mjs"]

