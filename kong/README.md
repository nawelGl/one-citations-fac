# Exemple d'implémentation d'API Manager

Pour cet exemple on va utiliser [Kong](https://konghq.com/kong/) comme api manager avec [cassandra](http://cassandra.apache.org/) en base de données.

## Initialisation
Démarrer la base de données :
```console
$ docker-compose up -d kong-base
```

Ensuite il faut initialiser la base 
```console
$ docker-compose up -d cassandra-init
```

Enfin on peut démarrer kong
```console
$ docker-compose up -d kong
``` 

## Configuration
### Ajouter un service
```console
curl -i -X POST \
  --url http://localhost:8001/services/ \
  --data 'name=fish' \
  --data 'url=https://hubeau.eaufrance.fr/api'
```

### Ajouter une route
```console
curl -i -X POST \
    --url http://localhost:8001/services/fish/routes \
    --data 'paths[]=/fish'
```
On teste la route
```console
curl -X GET --url http://localhost:8000/fish/v0/etat_piscicole/code_espece_poisson
```

### Add a consumer
```console
curl -d "username=guillaume" http://localhost:8001/consumers
````

Puis on crée la clé
```console
curl -X POST http://localhost:8001/consumers/guillaume/key-auth -d ''
```

### Add plugins
```console
curl -X POST http://localhost:8001/routes/{id}/plugins \
    --data "name=key-auth" 
```
