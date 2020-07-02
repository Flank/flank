# Flank permissions denied behavior

Reported on: [Clean flank not authorized error messages #874](https://github.com/Flank/flank/issues/874)

Changed on: [Enhance permission denied exception logs #875](https://github.com/Flank/flank/pull/875)

## 1. User don't have permission to project (403)

When user don't have permission to project Flank should returns message like:

```json

Flank encountered a 403 error when running on project $project_name. Please verify this credential is authorized for the project.
Consider authentication a with Service Account https://github.com/Flank/flank#authenticate-with-a-service-account
or with a Google account https://github.com/Flank/flank#authenticate-with-a-google-account

Caused by: com.google.api.client.googleapis.json.GoogleJsonResponseException: 403 Forbidden
{
  "code" : 403,
  "errors" : [ {
    "domain" : "global",
    "message" : "The caller does not have permission",
    "reason" : "forbidden"
  } ],
  "message" : "The caller does not have permission",
  "status" : "PERMISSION_DENIED"
}

```

## 2. Project not found (404)

When project not found on firebase Flank should return message like:

```json

Flank was unable to find project $project_name. Please verify the project id.

Caused by: com.google.api.client.googleapis.json.GoogleJsonResponseException: 404 Not Found
{
  "code" : 404,
  "errors" : [ {
    "domain" : "global",
    "message" : "Project not found: $project_name",
    "reason" : "notFound"
  } ],
  "message" : "Project not found: $project_name",
  "status" : "NOT_FOUND"
}

```

## 3. On this two cases Flank throws FlankCommonException and exit with code: 1
