# SDX Gateway
The Survey Data Exchange (SDX) Gateway is a RESTful web service implemented using [Spring Boot](http://projects.spring.io/spring-boot/). It provides an interface for the Survey Data Exchange to notify Response Management when a questionnaire has been receipted.

[![Build Status](https://travis-ci.org/ONSdigital/rm-sdx-gateway.svg?branch=master)](https://travis-ci.org/ONSdigital/rm-sdx-gateway)

The [Swagger](http://swagger.io/) specification that documents the SDX Gateway's API can be found in `/swagger.yml`.

## Building SDX Gateway
The code in this repository depends on some common framework code in the [rm-common-service](https://github.com/ONSdigital/rm-common-service) repository. Build that code first then use the command below to build SDX Gateway:

```
mvn --update-snapshots
```

## Copyright
Copyright (C) 2016 Crown Copyright (Office for National Statistics)
