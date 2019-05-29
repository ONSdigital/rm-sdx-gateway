# SDX Gateway API
This page documents the SDX Gateway API endpoints. Apart from the Service Information endpoint, all these endpoints are secured using HTTP basic authentication. All endpoints return an `HTTP 200 OK` status code except where noted otherwise.

## Service Information
* `GET /info` will return information about this service, collated from when it was last built.

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

## Create Response Receipt
* `POST /receipts` will create a response receipt.

**Required parameters**: `caseId` as the ID of the case the response receipt is for.

An `HTTP 201 Created` status code is returned if the response receipt creation was a success. An `HTTP 400 Bad Request` is returned if the required parameter is missing.

## Acknowledge File
* `POST /paperquestionnairereceipts` will acknowledge a paper response file.

**Required parameters**: `file` as the paper response file received.

An `HTTP 201 Created` status code is returned if the file was read.