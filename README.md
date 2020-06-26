[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e62c6d31e0ec427e9e1c303d6c7dd744)](https://www.codacy.com/app/sdcplatform/rm-sdx-gateway?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ONSdigital/rm-sdx-gateway&amp;utm_campaign=Badge_Grade) [![Docker Pulls](https://img.shields.io/docker/pulls/sdcplatform/sdx-gateway.svg)]()
[![Build Status](https://travis-ci.org/ONSdigital/rm-sdx-gateway.svg?branch=master)](https://travis-ci.org/ONSdigital/rm-sdx-gateway)

# SDX Gateway
The Survey Data Exchange (SDX) Gateway is a RESTful web service implemented using [Spring Boot](http://projects.spring.io/spring-boot/). It provides an interface for the Survey Data Exchange to notify Response Management when a response has been receipted.

When surveys are completed, EQ sends them to SDX: this is currently the only way that RM knows that a user has finished a survey.

Receipts are created by SDX posting to the `/receipts` endpoint, defined in `ReceiptEndpoint`.

The endpoint for paper receipts from SDX is `/paperquestionnairereceipts`, defined in `PaperReceiptEndpoint`.

Once SDX calls this gateway, case receipts are then published to the RabbitMQ queue "caseReceipt" by `CaseReceiptPublisherImpl`.

## Running

There are two ways of running this service.
The easiest way is via docker (https://github.com/ONSdigital/ras-rm-docker-dev)

Alternatively, you can run the service locally. For this, you'll need redis server running in a second window (port is currently set to 7379). 
```bash
redis-server /usr/local/etc/redis.conf
```
You'll also need postgres and some other backend services running. For these you can cd to the ras-rm-docker-dev repo in a third window, and run:
```bash
$ docker-compose -f dev.yml up
```
With those up and running, in your first window run:
```bash
$ mvn clean install
$ mvn spring-boot:run
```

# Code Styler
To use the code styler please goto this url (https://github.com/google/google-java-format) and follow the Intellij instructions or Eclipse depending on what you use

### Overriding Credentials

    ./mvnw sprint-boot:run -Dsecurity.user.name=tiptop -Dsecurity.user.password=secret

## API
See `swagger.yaml` for API documentation.

### Example JSON Response
```json
{
  "name": "sdxgatewaysvc",
  "version": "10.43.0",
  "origin": "git@github.com:ONSdigital/rm-sdx-gateway.git",
  "commit": "5e5bd444c2e890de05feb7e564fe2d17345e258b",
  "branch": "master",
  "built": "2017-07-17T09:49:18Z"
}
```

## Testing
See `src/test/resources/curlTests.txt`

## Potential improvements
The obsolete FTP code can be removed, including from:
- application-test.properties
- deployment.yaml
- pom.xml
- values.yaml
- AppConfig.java  
- Sftp.java
- SFTPPaperReceiptReceiver.java
- SFTPPaperReceiptReceiverImpl.java

## Copyright
Copyright (C) 2017 Crown Copyright (Office for National Statistics)
