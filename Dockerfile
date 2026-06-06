FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM caddy:latest
WORKDIR /app
RUN apt-get update && apt-get install -y openjdk-21-jre-headless && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/target/*.jar app.jar
COPY Caddyfile /etc/caddy/Caddyfile
EXPOSE 80 443 8080
CMD caddy run --config /etc/caddy/Caddyfile & java -jar app.jar