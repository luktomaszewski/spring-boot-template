FROM eclipse-temurin:24.0.1_9-jdk-alpine

WORKDIR /app

# copy gradle wrapper files
COPY gradlew .
COPY gradle gradle

RUN ./gradlew --version

# copy app code
COPY . .

# build app
RUN ./gradlew bootJar
