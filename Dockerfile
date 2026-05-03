# BUILD stage
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# PROD stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/process.jar process.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","process.jar"]