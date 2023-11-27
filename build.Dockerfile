FROM eclipse-temurin:21.0.1_12-jdk

WORKDIR /app

# copy gradle wrapper files
COPY gradlew .
COPY gradle gradle

RUN ./gradlew --version

# copy app code
COPY . .

# build app
RUN ./gradlew bootJar
