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
TMDb API → DTO → Service (konvertering) → Entity → DAO → PostgreSQL

## Teknologier
Java 25, Hibernate/JPA,
PostgreSQL, Jackson, Lombok,
JUnit 5, Testcontainers, Mockito, SLF4J/Logback

## Funktionalitet
List alle film / skuespillere / instruktører / genrer
List film inden for en given genre
Søg film på titel (case-insensitive, substring)
Opret, opdater og slet film
Gennemsnitsrating, top-10 højest/lavest vurderet, top-10 mest populære