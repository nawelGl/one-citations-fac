#!/bin/bash

cd "$(dirname "$0")/.." || exit

echo "Arret de tous les conteneurs..."
docker compose -f docker-compose.yml down

echo "Nettoyage termine."