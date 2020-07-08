FROM openjdk:11-jre-slim

ARG JAR_FILE=sdx-gateway.jar
RUN apt-get update

COPY target/$JAR_FILE /opt/$JAR_FILE

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/sdx-gateway.jar" ]

