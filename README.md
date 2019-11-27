# Spring Boot Template

* Spring Boot 2.1.9.RELEASE + Web + JPA + REST
* Gradle Wrapper
* Lombok
* MapStruct
* Swagger
* H2 (embedded database)
* JUnit 5
* JaCoCo Java Code Coverage

## How to run
#### using docker-compose

*REQUIRED: Docker running*

###### Starting
```
./gradlew clean build
docker-compose up --build -d
```

###### Closing
```
docker-compose down --remove-orphans
```

#### using gradle wrapper
```
./gradlew clean bootRun
```

## Swagger
* `http://localhost:4326/v2/api-docs` - API Docs [JSON]
* `http://localhost:4326/swagger-ui.html` - Swagger UI