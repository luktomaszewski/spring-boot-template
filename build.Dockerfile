FROM eclipse-temurin:22.0.1_8-jdk-jammy

WORKDIR /app

# copy gradle wrapper files
COPY gradlew .
COPY gradle gradle

RUN ./gradlew --version

# copy app code
COPY . .

# build app
RUN ./gradlew bootJar
