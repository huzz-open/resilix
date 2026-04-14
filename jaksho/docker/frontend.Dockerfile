FROM node:22-alpine AS build
WORKDIR /app
COPY jaksho-fe/package.json jaksho-fe/package-lock.json ./
RUN npm ci
COPY jaksho-fe/ .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
