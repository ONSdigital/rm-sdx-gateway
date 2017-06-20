FROM openjdk:8-jdk 
MAINTAINER Kieran Wardle <kieran.wardle@ons.gov.uk>
ARG jar
VOLUME /tmp
COPY $jar sdx-gateway.jar
RUN sh -c 'touch /sdx-gateway.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java -jar /sdx-gateway.jar" ]

