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
Default is for the DEV environment (wsdls pointing ot the SoapUI mock service).
./mvnw clean install


##################################################
# To run the app
##################################################
- Prerequisites:
    - for logging:
        - cd /var/log/ctp/responsemanagement
        - mkdir sdxgateway
        - chmod 777 sdxgateway
./mvnw spring-boot:run


##################################################
# To test
##################################################
## To test the health endpoint
curl http://localhost:8291/mgmt/health -v -X GET
{"status":"UP","diskSpace":{"status":"UP","total":30335164416,"free":7011123200,"threshold":10485760}}


## To post an invalid receipt
curl -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8191/questionnairereceipts -v -X POST -d "{\"firstName\":\"Lionel\",\"lastName\":\"Messi\"}"
400


## To post a valid receipt
curl -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8191/questionnairereceipts -v -X POST -d "{\"caseId\":1}"
204


## Copyright
Copyright (C) 2016 Crown Copyright (Office for National Statistics)


## TODO list
Switch to Undertow
Error handling for the 400, etc
More details returned with health endpoint: build number, etc.
Add Sleuth

