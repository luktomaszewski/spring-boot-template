FROM spring-boot-template:builder AS builder

FROM eclipse-temurin:22.0.1_8-jre-alpine AS runtime

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
