FROM maven:3.9.9-eclipse-temurin-24-alpine as build
COPY . .
RUN mvn clean package -DskipTests
LABEL authors="jonah"


FROM openjdk:21-jdk
COPY --from=build /target/*.jar risk.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/risk.jar"]