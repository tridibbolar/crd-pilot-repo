FROM openjdk:8u131-jre-alpine

MAINTAINER tcgdigital

EXPOSE 8080

WORKDIR /tmp

COPY /target/rest-template-1.0.jar /tmp

CMD ["java","-jar","rest-template-1.0.jar"]


