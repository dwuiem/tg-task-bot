FROM maven:3.9.9-eclipse-temurin-17 AS build

COPY src src
COPY pom.xml pom.xml

RUN mvn clean package dependency:copy-dependencies -DincludeScope=runtime

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /target/core-service-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]