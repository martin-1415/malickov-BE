# -------- Build Stage --------
FROM ubuntu:24.04 AS builder

# Set the working directory
WORKDIR /app

RUN   apt update -y && apt upgrade -y && apt install -y maven &&  apt install -y libgtk2.0-dev libgtk-3-dev && apt install -y openjdk-21-jdk

# Copy the source code
COPY . .

# Build the Spring Boot application
RUN mvn clean package -DskipTests


# -------- Run Stage --------

FROM ubuntu:24.04

WORKDIR /app

RUN   apt update -y && apt upgrade -y && apt install -y openjdk-21-jdk && apt install -y libgtk2.0-0

COPY --from=builder /app/src/main/resources/yolo3/ /app/src/main/resources/yolo3/
COPY --from=builder /app/target/process.jar process.jar

ENTRYPOINT ["java"]
CMD ["-jar", "process.jar"] 

