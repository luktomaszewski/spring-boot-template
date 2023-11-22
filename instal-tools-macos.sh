#!/bin/bash

brew update

brew install make
brew install --cask docker
brew install kubectl
brew install helm
brew install pre-commit
brew install hadolint
brew install jq
brew install awscli-local

echo "make version:" && make --version
echo "pre-commit version:" && pre-commit --version
echo "docker version:" && docker --version
echo "kubectl version:" && kubectl version --client
echo "helm version:" && helm version
echo "hadolint version:" && hadolint --version
echo "jq version:" && jq --version
echo "awslocal version:" && awslocal --version
