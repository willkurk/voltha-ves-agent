FROM maven:3-jdk-8 as maven
COPY . /mavenwd
WORKDIR /mavenwd/
RUN mvn -f /mavenwd/pom.xml clean package

FROM openjdk:8-jre-alpine
RUN mkdir -p /opt/ves-agent && chmod 777 -R /opt/ves-agent
VOLUME /tmp
COPY --from=maven /mavenwd/config/config.properties /opt/ves-agent/config.properties
COPY --from=maven /mavenwd/target/ves-agent-0.1.0.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
