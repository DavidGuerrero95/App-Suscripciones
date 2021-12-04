FROM openjdk:12
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} Suscripciones.jar
ENTRYPOINT ["java","-jar","/Suscripciones.jar"]