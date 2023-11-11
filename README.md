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
* `awslocal`

## :rocket: Getting started

To see a list of available commands, run command:

```bash
make
```

## Actuator

* [`http://localhost:4326/actuator/health`](http://localhost:4326/actuator/health) - health information (status)
* [`http://localhost:4326/actuator/info`](http://localhost:4326/actuator/info) - application basic information

## API Documentation

* [`http://localhost:4326/v3/api-docs`](http://localhost:4326/v3/api-docs) - API Docs [JSON]
* [`http://localhost:4326/v3/api-docs.yaml`](http://localhost:4326/v3/api-docs.yaml) - API Docs [YAML]
* [`http://localhost:4326/swagger-ui.html`](http://localhost:4326/swagger-ui.html) - Swagger UI

## How to reuse template

### Spring Boot

- [ ] [`settings.gradle`](settings.gradle):
  * change `rootProject.name` value
- [ ] [`build.gradle`](build.gradle):
  * change `group` value
- [ ] [`OpenApiConfig.java`](src/main/java/com/lomasz/spring/boot/template/config/OpenApiConfig.java):
  * change description in `openApi()`
- [ ] [`src/main`](src/main):
  * rename main package value
- [ ] [`application.properties`](src/main/resources/application.properties):
  * change `server.port` value

### Docker

- [ ] [`docker-compose.yml`](docker-compose.yml):
  * change image name
  * change port mapping

### Helm

- [ ] [`values.yaml`](values.yaml)
  * change `image.repository` value
  * change `service.port` value
- [ ] [`localstack.yaml`](localstack.yaml)
  * change `image.repository` value
  * change `service.port` value

### Make

- [ ] [`Makefile`](Makefile)
  * change `NAMESPACE` value
  * change `APP_NAME` value
  * change `APP_PORT` value
