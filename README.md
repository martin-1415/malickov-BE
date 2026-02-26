# Malickov BE



## Local Development Setup

To start a local development server, run:

```bash
 docker pull docker4asinkan/malickov_sql:v01 
```

```bash
sudo docker run --name malickov-db -e POSTGRES_PASSWORD=mypassword -e POSTGRES_USER=BEuser -p 5432:5432 -d docker4asinkan/malickovdb:v01
```
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```
The default password for users predefined in dev databases is "abc"

## Open API

To see open API

[api schema](https://localhost:8443/swagger-ui/index.html)


or for json

[json scheme](https://localhost:8443/v3/api-docs)


## SQL Container
To enter running SQL server

```bash
docker exec -it malickov-db /bin/sh
```
or connect directly to the psql with password mypassword
```bash
psql -U BEuser -h localhost -p 5432 malickov
```

## Building

To build the project run:

```bash
mvn clean package 
```
To skipp tests
```bash
mvn clean package -DskipTests
```

## Test

To run tests:

```bash
mvn test -Dtest=UserControlerTest
```


## Prod server

Not implemented yet.
