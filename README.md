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
    - Stop RabbitMQ if running: sudo /sbin/service rabbitmq-server stop
    - Install ActiveMQ:
        - Install Apache ActiveMQ 5.13.3: download and unzip under /opt
        - Edit /conf/activemq.xml: replace 61616 with 53445 (port defined in broker-int.xml)
        - Start it by going to /bin and typing: ./activemq console
        - console accessed at http://localhost:8161/ with user = admin - pwd = admin

./mvnw spring-boot:run


##################################################
# To test
##################################################
## To test the info endpoint
curl http://localhost:8291/mgmt/info -v -X GET
200 {"contactEmail":"philippe.brossier@ons.gov.uk","version":"${project.version}","commit":"${buildNumber}","branch":"${scmBranch}","buildTime":"${timestamp}"}


## To post an invalid receipt (missing caseRef)
curl -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8191/questionnairereceipts -v -X POST -d "{\"firstName\":\"Lionel\",\"lastName\":\"Messi\"}"
TODO Should it be a CTPException one - 400 {"timestamp":1475589808417,"status":400,"error":"Bad Request","message":"Bad Request","path":"/questionnairereceipts"}


## To post an invalid receipt (caseRef is not a string)
curl -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8191/questionnairereceipts -v -X POST -d "{\"caseRef\":6}"
TODO 500 {"error":{"code":"SYSTEM_ERROR","timestamp":"20161004150454125","message":"failed to look up MessageChannel with name 'caseFeedbackOutbound' in the BeanFactory.; nested exception is org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'caseFeedbackOutbound' is defined"}}


## To post a valid receipt
curl -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8191/questionnairereceipts -v -X POST -d "{\"caseRef\":\"abc\"}"
TODO {"error":{"code":"SYSTEM_ERROR","timestamp":"20161004150553968","message":"failed to look up MessageChannel with name 'caseFeedbackOutbound' in the BeanFactory.; nested exception is org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'caseFeedbackOutbound' is defined"}}


## Copyright
Copyright (C) 2016 Crown Copyright (Office for National Statistics)


## TODO list
- info endpoint details: see curl test above
- Update Swagger spec for caseRef
- Add Sleuth

