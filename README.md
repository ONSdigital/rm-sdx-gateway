# SDX Gateway
The Survey Data Exchange (SDX) Gateway is a RESTful web service implemented using [Spring Boot](http://projects.spring.io/spring-boot/). It provides an interface for the Survey Data Exchange to notify Response Management when a questionnaire has been receipted.

[![Build Status](https://travis-ci.org/ONSdigital/rm-sdx-gateway.svg?branch=master)](https://travis-ci.org/ONSdigital/rm-sdx-gateway)

The [Swagger](http://swagger.io/) specification that documents the SDX Gateway's API can be found in `/swagger.yml`.


##################################################
# To add all the necessary Maven Wrapper files
#
# This is only run once and has already been run.
##################################################
mvn -N io.takari:maven:wrapper


##################################################
# To build
##################################################
./mvnw clean install -P artifactory-aws


##################################################
# To run the app
##################################################
- Prerequisites:
    - Start RabbitMQ: sudo /sbin/service rabbitmq-server start

- To start with default credentials:
    ./mvnw spring-boot:run

- To start with specific credentials:
    ./mvnw spring-boot:run -Dsecurity.user.name=tiptop -Dsecurity.user.password=override


##################################################
## To test
##################################################
See curlTests.txt under /test/resources


## Copyright
Copyright (C) 2016 Crown Copyright (Office for National Statistics)
