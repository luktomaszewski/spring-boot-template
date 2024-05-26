FROM spring-boot-template:builder AS builder

FROM eclipse-temurin:21.0.3_9-jre-alpine AS runtime

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
