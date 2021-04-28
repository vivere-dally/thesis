FROM openjdk:11-jdk-slim

RUN mkdir -p /home/app

COPY ./target/*.jar /home/app/application.jar

WORKDIR /home/app

CMD ["java", "-jar", "application.jar"]
