FROM openjdk:11.0.7-jre-slim
ADD build/libs/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
