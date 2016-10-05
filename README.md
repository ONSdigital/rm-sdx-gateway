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
Default is for the DEV environment
./mvnw clean install
./mvnw clean install -DskipITs (if you want to skip integration tests which are run by default with the dev profile)


##################################################
# To run the app
##################################################
- Prerequisites:
    - for logging:
        - cd /var/log/ctp/responsemanagement
        - mkdir sdxgateway
        - chmod 777 sdxgateway
    - Stop RabbitMQ if running: sudo /sbin/service rabbitmq-server stop
    - Install ActiveMQ:
        - Install Apache ActiveMQ 5.13.3: download and unzip under /opt
        - Edit /conf/activemq.xml: replace 61616 with 53445 (port defined in broker-int.xml)
        - Start it by going to /bin and typing: ./activemq console
        - console accessed at http://localhost:8161/ with user = admin - pwd = admin

- To start with default credentials:
    ./mvnw spring-boot:run

- To start with specific credentials:
    ./mvnw spring-boot:run -Dsecurity.user.name=tiptop -Dsecurity.user.password=override


##################################################
# To test
##################################################
## To test the info endpoint WITHOUT credentials
401 {"timestamp":1475607716095,"status":401,"error":"Unauthorized","message":"Full authentication is required to access this resource","path":"/mgmt/health"}


## To test the info endpoint
curl http://localhost:8291/mgmt/info -v -X GET -u admin:ctp
200 {"contactEmail":"philippe.brossier@ons.gov.uk","version":"9.26.0-SNAPSHOT","commit":"162cf42","branch":"master","buildTime":"16:10:00 BST on 04 October 2016"}


## To test the health endpoint
curl http://localhost:8291/mgmt/health -v -X GET -u admin:ctp
200 {"status":"UP"}


## To test the env endpoint
curl http://localhost:8291/mgmt/env -v -X GET -u admin:ctp
200 long json


## To post an invalid receipt (missing caseRef)
curl -u admin:ctp -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8191/questionnairereceipts -v -X POST -d "{\"firstName\":\"Lionel\",\"lastName\":\"Messi\"}"
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20161004212343106","message":"Provided json is incorrect."}}


## To post an invalid receipt (empty caseRef)
curl -u admin:ctp -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8191/questionnairereceipts -v -X POST -d "{\"caseRef\":\"\"}"
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20161004212425903","message":"Provided json fails validation."}}


## To post a valid receipt
curl -u admin:ctp -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8191/questionnairereceipts -v -X POST -d "{\"caseRef\":\"abc\"}"
201 {"caseRef":"abc"} and header Location: http://localhost:8191/questionnairereceipts/abc and verify +1 on queue Case.Responses at http://localhost:8161/admin/queues.jsp


## To post a valid daily .csv file
cd /home/centos/code/rm-sdx-gateway/src/test/resources/dailyPaperFiles
curl -u admin:ctp http://localhost:8191/paperquestionnairereceipts -v -X POST -F file=@sampleAllThreeValidReceipts.csv
201 and verify +3 on queue Case.Responses at http://localhost:8161/admin/queues.jsp


## To post an invalid daily .csv file
cd /home/centos/code/rm-sdx-gateway/src/test/resources/dailyPaperFiles
curl -u admin:ctp http://localhost:8191/paperquestionnairereceipts -v -X POST -F file=@totalRandom.txt
400 {"error":{"code":"VALIDATION_FAILED","timestamp":"20161005110825620","message":"An unexpected error occured while acknowledging your receipts file. No receipt found for acknowledgment"}}


## Copyright
Copyright (C) 2016 Crown Copyright (Office for National Statistics)
