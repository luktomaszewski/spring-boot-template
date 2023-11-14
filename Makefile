APP_SERVICE_NAME=app
BUILDER_SERVICE_NAME=builder

APP_NAME=spring-boot-template
APP_PORT=4326

NAMESPACE=spring-boot-template
HELM_CHART=helm-chart

help:
	@grep -E '^[1-9a-zA-Z_-]+:.*?## .*$$|(^#--)' $(MAKEFILE_LIST) \
	| awk 'BEGIN {FS = ":.*?## "}; {printf "\033[32m %-43s\033[0m %s\n", $$1, $$2}' \
	| sed -e 's/\[32m #-- /[33m/'

#-- docker/docker-compose:
.PHONY: up
up: ## start the app
	docker-compose up $(APP_SERVICE_NAME)

.PHONY: down
down: ## stop the app, any running contains, and networking
	docker-compose down

.PHONY: debug
debug: ## debug the service container with app by running docker and shelling into it
	docker-compose exec -it $(APP_SERVICE_NAME) //bin/bash

.PHONY: build
build: ## build docker image
	docker-compose build $(BUILDER_SERVICE_NAME)
	docker-compose build $(APP_SERVICE_NAME)

.PHONY: destroy
destroy: ## remove the app and all containers, images and volumes
	docker-compose down -v --rmi all

#-- helm/k8s:
.PHONY: helm-lint
helm-lint: ## lint helm chart
	helm lint $(HELM_CHART)

.PHONY: helm-install
helm-install: ## install helm chart (default)
	helm upgrade -i $(APP_NAME) $(HELM_CHART) -n $(NAMESPACE) --values local.values.yaml --create-namespace

.PHONY: helm-delete
helm-delete: ## uninstall helm chart
	helm delete $(APP_NAME) -n $(APP_NAME)

.PHONY: debug-pod
debug-pod: ## debug pod
	POD_NAME=$(kubectl get pods -n $(APP_NAME) -o json | jq -r '.items[].metadata.name')
	kubectl exec --stdin --tty -n $(NAMESPACE) ${POD_NAME} -- /bin/bash

.PHONY: port-forward
port-forward: ## make port forward to k8s service
	kubectl port-forward -n $(NAMESPACE) svc/$(APP_NAME) $(APP_PORT):$(APP_PORT)

#-- localstack:
AWS_ECR_URI=localhost.localstack.cloud:4510
DOCKER_ECR_TAG="$(AWS_ECR_URI)/$(APP_NAME)"

.PHONY: ecr-login
ecr-login: ## login to ECR
	awslocal ecr get-login-password | docker login --username AWS --password-stdin $(AWS_ECR_URI)

.PHONY: ecr-publish
ecr-publish: build ecr-login ## publish docker image to ECR
	docker tag $(APP_NAME):latest $(DOCKER_ECR_TAG):latest
	docker push $(DOCKER_ECR_TAG):latest

.PHONY: ecr-images
ecr-images: ## list docker images in ECR repository
	awslocal ecr describe-images --repository-name $(APP_NAME)

.PHONY: helm-install-localstack
helm-install-localstack: ## install helm chart on EKS in LocalStack
	helm upgrade -i $(APP_NAME) $(HELM_CHART) -n $(NAMESPACE) --values localstack.values.yaml --create-namespace
