FROM openjdk:8-jre-slim

COPY sdxgatewaysvc.jar /opt/sdxgatewaysvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/sdxgatewaysvc.jar" ]