FROM openjdk:11.0.7-jre-slim

LABEL maintainer="lomasz"
LABEL name="Spring Boot Template"
LABEL version="0.0.1-SNAPSHOT"
LABEL description="Spring Boot Template"

COPY build/libs/*.jar /app.jar

EXPOSE 4326

USER 666:666

ENTRYPOINT ["java", "-jar", "/app.jar"]
