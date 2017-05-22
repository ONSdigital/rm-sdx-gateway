FROM openjdk 
ARG jar
VOLUME /tmp
ADD $jar sdx-gateway.jar
RUN sh -c 'touch /sdx-gateway.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java -jar /sdx-gateway.jar" ]

