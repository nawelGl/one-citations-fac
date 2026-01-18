#!/bin/bash

GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

cd "$(dirname "$0")/.." || exit

mkdir -p logs-docker

echo -e "${BLUE}Demarrage de l'environnement Docker...${NC}"
echo "Les logs de construction sont sauvegardés dans logs-docker/build.log"

docker compose -f docker-compose.yaml up -d --build 2>&1 | tee logs-docker/build.log

if [ ${PIPESTATUS[0]} -eq 0 ]; then
    echo -e "${BLUE}Attente du demarrage des services (30 sec)...${NC}"
    sleep 30

    echo ""
    echo "=========================================================="
    echo -e "   ${GREEN}TOUT EST LANCÉ VIA DOCKER${NC}"
    echo "=========================================================="
    echo -e "Swagger Central  :  ${BLUE}http://localhost:8888${NC}"
    echo -e "Keycloak         :  ${BLUE}http://localhost:8080${NC}"
    echo "----------------------------------------------------------"
    echo "Pour voir les logs d'une API specifique en direct :"
    echo "docker compose logs -f profiles-api"
    echo "docker compose logs -f citations-api"
    echo "docker compose logs -f images-api"
    echo "=========================================================="
else
    echo -e "${RED}Il y a eu une erreur lors du lancement. Verifie logs-docker/build.log${NC}"
fi