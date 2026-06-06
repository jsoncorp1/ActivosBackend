# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Caddy + Java
FROM caddy:latest
WORKDIR /app

# Instalar Java
RUN apt-get update && apt-get install -y openjdk-21-jre-headless && rm -rf /var/lib/apt/lists/*

# Copiar JAR del build
COPY --from=build /app/target/*.jar app.jar

# Copia Caddyfile
COPY Caddyfile /etc/caddy/Caddyfile

# Exponer puertos
EXPOSE 80 443 8080

# Start Caddy (que a su vez inicia la app Java)
CMD caddy run --config /etc/caddy/Caddyfile