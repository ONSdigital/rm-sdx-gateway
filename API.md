# SDX Gateway API
This page documents the SDX Gateway API endpoints. All endpoints return an `HTTP 200 OK` status code except where noted otherwise.

## Create Response Receipt
* `POST /receipts` will create a response receipt.

**Required parameters**: `caseId` as the ID of the case the response receipt is for.

An `HTTP 201 Created` status code is returned if the response receipt creation was a success. An `HTTP 400 Bad Request` is returned if the required parameter is missing.
