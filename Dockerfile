FROM gcr.io/distroless/java:11

LABEL maintainer="lomasz"
LABEL name="Spring Boot Template"
LABEL version="0.0.1-SNAPSHOT"
LABEL description="Spring Boot Template"

COPY build/libs/*.jar /app.jar

EXPOSE 4326

USER 666:666

ENTRYPOINT ["java", "-jar", "/app.jar"]
