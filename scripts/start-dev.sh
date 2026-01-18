#!/bin/bash

GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

cd "$(dirname "$0")/.." || exit

mkdir -p logs-scripts

echo -e "${BLUE}Demarrage de l'infrastructure Docker...${NC}"
docker compose up -d mongo keycloak imgproxy swagger

echo -e "${BLUE}Attente de 5 secondes...${NC}"
sleep 5

echo -e "${BLUE}Lancement de PROFILES (security,local)...${NC}"
cd profiles-api
./gradlew bootRun --args='--spring.profiles.active=security,local' > ../logs-scripts/logs_profiles.txt 2>&1 &
cd ..

echo -e "${BLUE}Lancement de CITATIONS...${NC}"
cd citations-api
./gradlew bootRun > ../logs-scripts/logs_citations.txt 2>&1 &
cd ..

echo -e "${BLUE}Lancement de IMAGES...${NC}"
cd images-api
./gradlew bootRun > ../logs-scripts/logs_images.txt 2>&1 &
cd ..

echo -e "${GREEN}Lancement effectue en arriere-plan.${NC}"
echo -e "${BLUE}Attente du demarrage des serveurs (15 sec)...${NC}"
sleep 15

echo ""
echo "=========================================================="
echo -e "   ${GREEN}RECAPITULATIF DES URLS${NC}"
echo "=========================================================="
echo -e "Keycloak Console :  ${BLUE}http://localhost:8080${NC}"
echo -e "Central Swagger  :  ${BLUE}http://localhost:8888${NC}"
echo "----------------------------------------------------------"
echo -e "Profiles Swagger :  ${BLUE}http://localhost:9090/swagger-ui/index.html${NC}"
echo -e "Citations Swagger:  ${BLUE}http://localhost:9091/swagger-ui/index.html${NC}"
echo -e "Images Swagger   :  ${BLUE}http://localhost:9092/swagger-ui/index.html${NC}"
echo "=========================================================="
echo "Les logs sont dans le dossier : logs-scripts/"
echo "Pour tout arreter : ./scripts/stop-dev.sh"
echo ""