FROM gradle:7.5.1-jdk17 AS builder

WORKDIR /home/gradle/src

COPY . .

RUN gradle jar

FROM eclipse-temurin:17-jre-jammy AS runtime

RUN groupadd -r geogeeks && useradd -r -s /bin/false -g geogeeks geogeek

LABEL maintainer="lomasz"
LABEL name="Spring Boot Template"
LABEL version="0.0.1-SNAPSHOT"
LABEL description="Spring Boot Template"

COPY --from=builder /home/gradle/src/build/libs/*.jar /app.jar

EXPOSE 4326

USER geogeek

ENTRYPOINT ["java", "-jar", "/app.jar"]
