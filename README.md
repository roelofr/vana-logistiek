# Vana Logistiek

> Ticketing voor logistiek van Castlefest

Dit is de Penis LogistiekApp voor team Logistiek van Castlefest.

Het is eigenlijk niet veel anders dan een ticketingsysteem om de communicatie tussen de teamleden en andere teams
te vereenvoudigen.

## Licentie

De software valt onder de [GNU General Public License v3](./LICENSE), en is dus open-source.
Gebruik voor andere doelen is toegestaan, maar er is geen mogelijkheid tot ondersteuning.

## Ontwikkelen

Vereist Java 21 en Maven, gebruik iet als [SDKman!][o1] om te installeren.

Daarnaast heb je Testcontainer nodig. Dit werkt samen met o.a. [Docker][o2] en [Podman][o3].

1. Clone de repository
2. Start de applicatie met `mvn quarkus:dev` of `./mvnw quarkus:dev`.
4. Ga naar `localhost:8081`

## Deployment

De applicatie kan ook in productie draaien (cool, hé!), met de nodige configuratie:

```properties
# Database config
DB_USERNAME=myuser
DB_PASSWORD=mypass
DB_URL=jdbc:mysql://localhost:3306/database

# OIDC config
OIDC_URL=https://auth.myvana.dev/
OIDC_CLIENT_ID=73ab90e3-34ea-44f9-ab90-e334ea04f91a
OIDC_CLIENT_SECRET=913b273f-6042-4816-bb27-3f60423816d8
```
