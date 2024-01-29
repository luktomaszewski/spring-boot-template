FROM eclipse-temurin:21.0.2_13-jdk-jammy

WORKDIR /app

# copy gradle wrapper files
COPY gradlew .
COPY gradle gradle

RUN ./gradlew --version

# copy app code
COPY . .

# build app
RUN ./gradlew bootJar
