FROM ubuntu:16.04
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y openjdk-8-jdk curl less kafkacat && \
    apt-get clean

RUN mkdir /opt/ves-agent && chmod a+rwx -R /opt/ves-agent
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
