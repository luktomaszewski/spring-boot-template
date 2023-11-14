# Spring Boot Template

![CI Status](https://github.com/lomasz/spring-boot-template/workflows/CI/badge.svg)

* spring boot + web + jpa + rest
* gradle wrapper
* lombok
* mapstruct
* h2 (embedded)
* junit 5
* jacoco
* k8s/helm
* make

## :memo: Prerequisites

* `make`
* `pre-commit`
* `docker` + `kubectl`
* `helm`
* `hadolint`
* `jq`
* `awslocal`

## :rocket: Getting started

To see a list of available commands, run command:

```bash
make
```

## How to reuse template

```bash
# Usage: ./generate.sh [--name app_name] [--port new_port] [--package new_package]
./generate.sh
```

Use the name of the current directory as the app name:

```bash
./generate.sh --name "$(basename "$(pwd)")"
```

## Actuator

* [`http://localhost:4326/actuator/health`](http://localhost:4326/actuator/health) - health information (status)
* [`http://localhost:4326/actuator/info`](http://localhost:4326/actuator/info) - application basic information

## API Documentation

* [`http://localhost:4326/v3/api-docs`](http://localhost:4326/v3/api-docs) - API Docs [JSON]
* [`http://localhost:4326/v3/api-docs.yaml`](http://localhost:4326/v3/api-docs.yaml) - API Docs [YAML]
* [`http://localhost:4326/swagger-ui.html`](http://localhost:4326/swagger-ui.html) - Swagger UI
