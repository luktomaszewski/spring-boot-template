# Spring Boot Template

![CI Status](https://github.com/lomasz/spring-boot-template/workflows/CI/badge.svg)
![CodeQL Status](https://github.com/lomasz/spring-boot-template/workflows/CodeQL/badge.svg)

* Spring Boot + Web + JPA + REST
* Gradle Wrapper
* Lombok
* MapStruct
* Swagger
* H2 (embedded database)
* JUnit 5
* JaCoCo Java Code Coverage
* k8s/Helm

## :memo: Prerequisites

* `make`
* `docker` + `kubectl`
* `helm`
* `pre-commit`
* `hadolint`
* `jq`

## :rocket: Getting started

You can run `make` to see a list of available commands.

## How to reuse template

- [ ] [`settings.gradle`](settings.gradle):
  * change **`rootProject.name`** value
- [ ] [`build.gradle`](build.gradle):
  * change **`group`** value
- [ ] [`OpenApiConfig.java`](src/main/java/com/lomasz/spring/boot/template/config/OpenApiConfig.java):
  * change description in **`swaggerApi()`** and **`CONTACT`**
- [ ] [`src/main`](src/main):
  * rename main package value
- [ ] [`application.properties`](src/main/resources/application.properties):
  * change **`server.port`** value
- [ ] [`Dockerfile`](Dockerfile):
  * modify all labels values
  * set `EXPOSE` value
- [ ] [`docker-compose.yml`](docker-compose.yml):
  * change image name
  * change port mapping
- [ ] [`docker-compose.yml`](docker-compose.yml):
  * change image name
  * change port mapping
- [ ] [`values.yaml`](values.yaml)
  * change `service.port` value
  * change
- [ ] [`Makefile`](Makefile)
  * change `NAMESPACE` value
  * change `APP_NAME` value
  * change `APP_PORT` value

## Actuator

* [`http://localhost:4326/actuator/health`](http://localhost:4326/actuator/health) - health information (status)
* [`http://localhost:4326/actuator/info`](http://localhost:4326/actuator/info) - application basic information

## API Documentation

* [`http://localhost:4326/v3/api-docs`](http://localhost:4326/v3/api-docs) - API Docs [JSON]
* [`http://localhost:4326/v3/api-docs.yaml`](http://localhost:4326/v3/api-docs.yaml) - API Docs [YAML]
* [`http://localhost:4326/swagger-ui.html`](http://localhost:4326/swagger-ui.html) - Swagger UI
