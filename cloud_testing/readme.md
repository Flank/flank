## Client libraries

Google provides [two types of client libraries:](https://cloud.google.com/apis/docs/client-libraries-explained) `Google API Client Libraries` and `Google Cloud Client Libraries`.

- `google-api-services-*` - legacy
  - `google-api-services-storage`
  - `google-api-services-toolresults`
  - `google-api-services-testing`
- `google-cloud-*` - newest
  - `google-cloud-storage`

## Discovery JSON

The discovery JSON is useful for understanding APIs and producing clients automatically. The discovery JSON contains the latest changes.

- [toolresults/v1beta3/](https://www.googleapis.com/discovery/v1/apis/toolresults/v1beta3/rest)
- [testing/v1](https://www.googleapis.com/discovery/v1/apis/testing/v1/rest) or [testing](https://testing.googleapis.com/$discovery/rest?version=v1)
- [storage/v1](https://www.googleapis.com/discovery/v1/apis/storage/v1/rest)
- `google-cloud-sdk\lib\googlecloudsdk\third_party\apis` has older JSON versions. Not recommended for use.
  - `storage_v1.json`
  - `toolresults_v1beta3.json`
  - `testing_v1.json`

See [client generation](client_generation.md) for more information.

## google-cloud-storage

Published on [maven central](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.google.cloud%22%20AND%20a%3A%22google-cloud-storage%22)

`com.google.cloud:google-cloud-storage:1.15.0`

- [google-cloud-storage github](https://github.com/GoogleCloudPlatform/google-cloud-java/tree/master/google-cloud-storage)
- [google-cloud-storage JavaDoc](http://googlecloudplatform.github.io/google-cloud-java/latest/apidocs/?com/google/cloud/storage/package-summary.html)

## google-api-services-toolresults

Published on [maven central](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.google.apis%22%20AND%20a%3A%22google-api-services-toolresults%22)

`com.google.apis:google-api-services-toolresults:v1beta3-rev351-1.21.0 `

- [Not on GitHub](https://github.com/google/google-api-java-client)
- [google-api-services-toolresults website](https://developers.google.com/api-client-library/java/apis/toolresults/v1beta3)
- [google-api-services-toolresults javadoc](https://developers.google.com/resources/api-libraries/documentation/toolresults/v1beta3/java/latest/index.html?overview-summary.html)

## google-api-services-testing

Client may be generated manually using `testing_v1.json` and the master branch of [apis-client-generator](https://github.com/google/apis-client-generator). Google also publishes official clients:

- [Java](https://developers.google.com/api-client-library/java/apis/testing/v1)
- [Python](https://developers.google.com/api-client-library/python/apis/testing/v1)
- [.NET](https://developers.google.com/api-client-library/dotnet/apis/testing/v1)
- [Ruby](https://developers.google.com/api-client-library/ruby/apis/testing/v1)
- [Go](https://github.com/google/google-api-go-client/tree/master/testing/v1)

## Clone
- `git clone https://android.googlesource.com/platform/tools/studio/google/cloud/testing`
- `git checkout studio-master-dev`
- [Android Studio repo](https://android.googlesource.com/platform/tools/studio/google/cloud/testing/+/studio-master-dev/firebase-testing/lib)

## Build

- `gradle build`

## Testing

API Discovery JSON may be converted to OpenAPI 3 using [apimatic.io/transformer](https://apimatic.io/transformer). [swagger-codegen 3](https://github.com/swagger-api/swagger-codegen/releases/tag/v3.0.0-rc0) is then used to generate a [swagger-inflector](https://github.com/swagger-api/swagger-inflector/) based mock server.

```
java -jar swagger-codegen-cli.jar generate \
  -i testing_openapi_3.json \
  -l inflector -o server/
```

The server is started locally with `gradle appRun`.

Visiting `http://localhost:8080/v1/projects/123/testMatrices/456` will return a random valid result based on the Swagger schema.

Toggle mock server via `FtlConstants.useMock`

- Gradle is added manually. [An issue](https://github.com/swagger-api/swagger-codegen/issues/7578) has been opened upstream.