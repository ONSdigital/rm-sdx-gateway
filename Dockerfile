FROM openjdk:8-jre-slim

ARG JAR_FILE=sdxgatewaysvc*.jar
RUN apt-get update && \
    apt-get -yq install curl

EXPOSE 8191

HEALTHCHECK --interval=1m30s --timeout=10s --retries=3 \
  CMD curl -f http://localhost:8191/info || exit 1

COPY target/$JAR_FILE /opt/sdx-gateway.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/sdx-gateway.jar" ]

