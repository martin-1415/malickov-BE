# Malickov BE



## Local Development Setup

To start a local development server, run:

```bash
 docker pull docker4asinkan/malickov_sql:v01 
```

```bash
docker run --name malickov-mysql -e MYSQL_ALLOW_EMPTY_PASSWORD=yes -p 3306:3306  -d docker4asinkan/malickov_sql:v01
```
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```
To see open API
[api schema](http://localhost:8080/swagger-ui.html)

or for json
[json scheme](http://localhost:8080/v3/api-docs)
## SQL Container
To enter running SQL server

```bash
docker exec -it malickov-mysql /bin/sh
```
and 
```bash
mysql
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



## Prod server

Not implemented yet.
