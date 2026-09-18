# repo auto created

# Danish Movies Backend

En Java-backend der henter danske film fra [TMDb API](https://www.themoviedb.org/),
gemmer dem i en PostgreSQL-database via JPA/Hibernate, og tilbyder
forskellige søge- og statistikfunktioner.

## Formål

Bygge en backend der kan:

1. Hente alle danske film fra de sidste 5 år fra TMDb (~1525 film)
2. Gemme film, genrer, skuespillere og instruktører i en database
3. Læse data ud igen på forskellige måder

Data hentes **to gange** fra API'et til at tjekke tidsforskel mellem mange
tråde og én tråd til at fetch data — derefter arbejder systemet kun mod
den lokale database.

## Arkitektur
