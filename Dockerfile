FROM openjdk:8-jre

COPY target/sdxgatewaysvc*.jar /opt/sdx-gateway.jar

ENTRYPOINT [ "java", "-jar", "/opt/sdx-gateway.jar" ]

