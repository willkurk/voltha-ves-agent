FROM ubuntu:16.04
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y openjdk-8-jdk curl less kafkacat && \
    apt-get clean

RUN mkdir /opt/ves-agent && chmod 777 -R /opt/ves-agent
VOLUME /tmp
ARG JAR_FILE
ARG PROPERTIES_FILE
COPY ${PROPERTIES_FILE} /opt/ves-agent/config.properties
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
