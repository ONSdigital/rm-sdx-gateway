FROM openjdk:8-jre

COPY target/sdxgatewaysvc*.jar /opt/sdx-gateway.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/sdx-gateway.jar" ]

