[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e62c6d31e0ec427e9e1c303d6c7dd744)](https://www.codacy.com/app/sdcplatform/rm-sdx-gateway?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ONSdigital/rm-sdx-gateway&amp;utm_campaign=Badge_Grade) [![Docker Pulls](https://img.shields.io/docker/pulls/sdcplatform/sdx-gateway.svg)]()
[![Build Status](https://travis-ci.org/ONSdigital/rm-sdx-gateway.svg?branch=master)](https://travis-ci.org/ONSdigital/rm-sdx-gateway)

# SDX Gateway
The Survey Data Exchange (SDX) Gateway is a RESTful web service implemented using [Spring Boot](http://projects.spring.io/spring-boot/). It provides an interface for the Survey Data Exchange to notify Response Management when a response has been receipted.

## Running
Ensure RabbitMQ is running.

    mvn clean install
    ./mvnw spring-boot:run

### Overriding Credentials

    ./mvnw sprint-boot:run -Dsecurity.user.name=tiptop -Dsecurity.user.password=secret

## API
See [API.md](https://github.com/ONSdigital/rm-sdx-gateway/blob/master/API.md) for API documentation.

## Testing
See `src/test/resources/curlTests.txt`

## Copyright
Copyright (C) 2017 Crown Copyright (Office for National Statistics)
