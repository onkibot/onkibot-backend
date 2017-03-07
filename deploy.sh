#!/bin/bash
: "${DRONE?Needs to be run in a Drone env}"

if [ "$DRONE_COMMIT_BRANCH" = "master" ]
then
	docker-compose -f docker-compose.prod.yml build web
	docker-compose -f docker-compose.prod.yml -p onkibot-backend-prod up --no-deps -d web
elif [ "$DRONE_COMMIT_BRANCH" = "development" ]
then
        docker-compose -f docker-compose.dev.yml build web
        docker-compose -f docker-compose.dev.yml -p onkibot-backend-dev up --no-deps -d web
fi
