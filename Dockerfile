FROM openjdk:17
ADD target/office-service.jar office-service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/office-service.jar"]