
FROM eclipse-temurin:21-jdk-alpine

RUN apk add --no-cache bash

WORKDIR /app

COPY . .

RUN chmod +x mvnw