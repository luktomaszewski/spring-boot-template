FROM spring-boot-template:builder AS builder

FROM eclipse-temurin:24.0.1_9-jre-alpine AS runtime

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
