FROM openjdk:8-jdk-alpine
ADD build/libs/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]