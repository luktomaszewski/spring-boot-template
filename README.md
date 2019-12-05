# Spring Boot Template 
![CI Status](https://github.com/lomasz/spring-boot-template/workflows/CI/badge.svg)

* Spring Boot 2.1.9.RELEASE + Web + JPA + REST
* Gradle Wrapper
* Lombok
* MapStruct
* Swagger
* H2 (embedded database)
* JUnit 5
* JaCoCo Java Code Coverage

## How to reuse template

* `settings.gradle`: change **`rootProject.name`** value
* `build.gradle`: change **`group`** value
* `src/main`: rename main package value 
* `application.properties`: change **`server.port`** value
* `docker-compose.yml`: change image name
* `docker-compose.yml`: change port mapping
* `SwaggerConfig.java`: change description in **`swaggerApi()`** and **`CONTACT`**

## How to run application

#### using gradle wrapper
```bash
./gradlew clean bootRun
```

#### using docker-compose
*REQUIRED: Docker running*

###### Start
```bash
./gradlew clean build
docker-compose up --build -d
```

###### Stop
```bash
docker-compose down --remove-orphans
```

## Actuator
* `http://localhost:4326/actuator/health` - health information (status)
* `http://localhost:4326/actuator/info` - application basic information

## API Documentation
* `http://localhost:4326/v2/api-docs` - API Docs [JSON]
* `http://localhost:4326/swagger-ui.html` - Swagger UI