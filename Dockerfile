FROM spring-boot-template:builder AS builder

FROM eclipse-temurin:21.0.1_12-jre-jammy AS runtime

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
