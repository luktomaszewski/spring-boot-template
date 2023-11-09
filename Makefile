APP_SERVICE_NAME=app
BUILDER_SERVICE_NAME=builder

NAMESPACE=spring-boot-template
APP_NAME=spring-boot-template
APP_PORT=4326

help:
	@grep -E '^[1-9a-zA-Z_-]+:.*?## .*$$|(^#--)' $(MAKEFILE_LIST) \
	| awk 'BEGIN {FS = ":.*?## "}; {printf "\033[32m %-43s\033[0m %s\n", $$1, $$2}' \
	| sed -e 's/\[32m #-- /[33m/'

#-- docker/docker-compose:
.PHONY: up
up: ## start the application
	docker-compose up $(APP_SERVICE_NAME)

.PHONY: down
down: ## stop the app, any running contains, and networking
	docker-compose down

.PHONY: debug
debug: ## debug the service container by running docker and shelling into it
	docker exec -it $(APP_SERVICE_NAME) //bin/bash

.PHONY: build
build: ## build docker image
	docker-compose build $(BUILDER_SERVICE_NAME)
	docker-compose build $(APP_SERVICE_NAME)

.PHONY: destroy
destroy: ## remove the app and all containers, images and volumes
	docker-compose down -v --rmi all

#-- helm/k8s:

.PHONY: helm-install
helm-install: ## install helm chart
	helm upgrade -i $(APP_NAME) helm-chart -n $(NAMESPACE) --values values.yaml --create-namespace

.PHONY: helm-delete
helm-delete: ## uninstall helm chart
	helm delete $(APP_NAME) -n $(APP_NAME) -f

.PHONY: helm-lint
helm-lint: ## lint helm chart
	helm lint helm-chart

.PHONY: debug-pod
debug-pod: ## debug pod
	POD_NAME=$(kubectl get pods -n $(APP_NAME) -o json | jq -r '.items[].metadata.name')
	kubectl exec --stdin --tty -n $(NAMESPACE) ${POD_NAME} -- /bin/bash

.PHONY: port-forward
port-forward: ## make port forward to k8s service
	kubectl port-forward -n $(NAMESPACE) svc/$(APP_NAME) $(APP_PORT):$(APP_PORT)
