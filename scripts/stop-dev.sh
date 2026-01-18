#!/bin/bash

cd "$(dirname "$0")/.." || exit

echo "Arret des applications Java..."
pkill -f 'java.*bootRun'

echo "Arret des conteneurs Docker..."
docker compose stop

echo "Suppression des fichiers de logs..."
rm -f logs-scripts/*.txt

echo "Arret complet termine."