# Vana Logistiek

> Ticketing voor logistiek van Castlefest

Dit is de Penis LogistiekApp voor team Logistiek van Castlefest.

Het is eigenlijk niet veel anders dan een ticketingsysteem om de communicatie tussen de teamleden en andere teams
te vereenvoudigen.

## Licentie

De software valt onder de [GNU General Public License v3](./LICENSE), en is dus open-source.
Gebruik voor andere doelen is toegestaan, maar er is geen mogelijkheid tot ondersteuning.

## Installatie

De makkelijkste installatie is met [Docker](https://docs.docker.com/engine/).

Hiervoor moet je wel een JWT signing key maken. Hiervoor heb je openssl nodig.
We gebruiken een ECDSA key met curve P-256.

```shell
mkdir jwt-certs
openssl genpkey -out jwt-certs/private-key.pem -algorithm ec -pkeyopt ec_paramgen_curve:P-256
openssl pkey -in jwt-certs/private-key.pem -pubout -out jwt-certs/public-key.pem
```

1. Download [`lib/docker-compose.yaml`](./lib/docker-compose.yaml)
2. Pas aan naar wens
3. `docker compose up -d`
4. Route een webserver naar `127.0.0.1:3000`
5. Ga naar de website

De standaard login credentials zijn `admin@example.com` met wachtwoord `AdminUser123`.

## Handmatige installatie

> **Warning**
> Dit is voor een dev-omgeving, voor productie-like, gebruik je de Docker images.

Vereist Java 21, Maven, Node en NPM.

1. Clone de repository
2. Configureer en start backend
    1. Kopieer `.env.example` naar `.env`
    2. Pas `.env` aan met database credentials (MySQL of MariaDB)
    3. Start met `mvn quarkus:run`
3. Configureer en start frontend
    1. Open een terminal in `frontend`
    2. Installeer dependencies met `npm install`
    3. Start met `npm start`
4. Ga naar `localhost:8001`
