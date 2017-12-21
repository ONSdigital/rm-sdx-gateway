ARG JAR_FILE=sdxgatewaysvc*.jar
FROM openjdk:8-jre

COPY target/$JAR_FILE /opt/sdx-gateway.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/sdx-gateway.jar" ]

