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


### DEV SQL Container
To enter running SQL server

```bash
sudo docker exec -it malickov-db /bin/sh
```
or connect directly to the psql with password mypassword
```bash
psql -U BEuser -h localhost -p 5432 malickov
```
to cancel sql command press 'q'



## Building

To build the the project run:

```bash
sudo docker build -t docker4asinkan/malickov_sql:v3.6 . 
```


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
```bash

java -jar malickov-be.jar \
  --spring.profiles.active=prod \
  --spring.datasource.username=BEuser \
  --spring.datasource.password=STRONG_PASSWORD \
  --security.secretKey=VERY_SECRET_KEY
```
### Database
To log as super user
```bash
 sudo -u postgres psql
```

Creata user and database
```bash
CREATE USER secretApplicationUser WITH PASSWORD;
```
Add user to database malickov

```bash
ALTER DATABASE malickov OWNER TO secretApplicationUser;

GRANT ALL PRIVILEGES ON DATABASE malickov TO secretApplicationUser;
```

To login to the database on the server use
```bash
psql -U secretApplicationUser -h publicIP -d malickov
```


GDPR compliance
type missmatch int and long for cookeis expiration date


