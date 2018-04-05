FROM openjdk:8-jre-slim

ARG JAR_FILE=sdxgatewaysvc*.jar
COPY target/$JAR_FILE /opt/sdx-gateway.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/sdx-gateway.jar" ]

