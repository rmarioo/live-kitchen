#!/bin/bash

# Check if PID is provided
if [ -z "$1" ]; then
  echo "Error: dockerfile is a mandatory parameter."
  echo "Usage: $0 <dockerfile>"
  echo "Example: $0 live_kitchen.Dockerfile"
  exit 1
fi

# Store the provided PID
DOCKERFILE_NAME=$1
IMAGE_NAME="${1%%.*}"

docker build --build-arg DATASOURCE_URL=jdbc:mysql://172.17.0.1:3306/recipes --build-arg CHEF_SERVER_URL=http://172.17.0.1:9992 --no-cache -f $DOCKERFILE_NAME -t $IMAGE_NAME .
