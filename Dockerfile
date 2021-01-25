FROM openjdk:11.0-jdk-slim

EXPOSE 8081

ARG JAR_FILE=target/aposentadoria-beneficiario-api-*.jar

WORKDIR /opt/app

COPY ${JAR_FILE} aposentadoria-beneficiario-api.jar

ENTRYPOINT ["java","-jar","aposentadoria-beneficiario-api.jar"]