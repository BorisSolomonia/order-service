FROM maven:latest
WORKDIR /order-service

COPY . /order-service
COPY . /src/main/resources/application.yaml
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/order-service/target/order-service-0.0.1-SNAPSHOT.jar"]