FROM openjdk:8-jre-slim

RUN groupadd --gid 999 sdxgw && \
    useradd --create-home --system --uid 999 --gid sdxgw sdxgw
USER sdxgw

COPY sdxgatewaysvc.jar /opt/sdxgatewaysvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/sdxgatewaysvc.jar" ]