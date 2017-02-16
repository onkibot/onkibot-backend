FROM java:8
MAINTAINER OnkiBOT Team <contact@onkibot.com>

RUN mkdir -p /app
COPY . /app/
WORKDIR /app

RUN set -e \
    && apt-get update \
    && apt-get install -y git \
    && apt-get autoremove -y \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
