## Client libraries

Google provides [two types of client libraries:](https://cloud.google.com/apis/docs/client-libraries-explained) `Google API Client Libraries` and `Google Cloud Client Libraries`.

- `google-api-services-*` - legacy
  - `google-api-services-storage`
  - `google-api-services-toolresults`
  - `google-api-services-testing` - source code not published
- `google-cloud-*` - newest
  - `google-cloud-storage`

## Discovery JSON

The discovery JSON is useful for understanding APIs and producing clients automatically. Note that the online discovery JSON may be very out of date. It's recommended to use the JSON descriptions included with the `google-cloud-sdk` for the most recent version.

- [toolresults/v1beta3/](https://www.googleapis.com/discovery/v1/apis/toolresults/v1beta3/rest)
- [testing/v1](https://www.googleapis.com/discovery/v1/apis/testing/v1/rest) or [testing](https://testing.googleapis.com/$discovery/rest?version=v1)
- [storage/v1](https://www.googleapis.com/discovery/v1/apis/storage/v1/rest)
- `google-cloud-sdk\lib\googlecloudsdk\third_party\apis` contains the JSON descriptions locally
  - `storage_v1.json`
  - `toolresults_v1beta3.json`
  - `testing_v1.json`

See [client generation](client_generation.md) for more information.

## google-cloud-storage

Published on [maven central](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.google.cloud%22%20AND%20a%3A%22google-cloud-storage%22)

`com.google.cloud:google-cloud-storage:1.2.0`

- [google-cloud-storage github](https://github.com/GoogleCloudPlatform/google-cloud-java/tree/master/google-cloud-storage)
- [google-cloud-storage JavaDoc](http://googlecloudplatform.github.io/google-cloud-java/0.20.0/apidocs/?com/google/cloud/storage/package-summary.html)

## google-api-services-toolresults

Published on [maven central](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.google.apis%22%20AND%20a%3A%22google-api-services-toolresults%22)

`com.google.apis:google-api-services-toolresults:v1beta3-rev242-1.22.0`

- [Not on GitHub](https://github.com/google/google-api-java-client)
- [google-api-services-toolresults website](https://developers.google.com/api-client-library/java/apis/toolresults/v1beta3)
- [google-api-services-toolresults javadoc](https://developers.google.com/resources/api-libraries/documentation/toolresults/v1beta3/java/latest/index.html?overview-summary.html)

## google-api-services-testing

Generated automatically from `gcloud-cli/googlecloudsdk/third_party/apis/testing_v1.json` using the master branch of [apis-client-generator](https://github.com/google/apis-client-generator). Not currently published by Google.

## Clone
- `git clone https://android.googlesource.com/platform/tools/studio/google/cloud/testing`
- `git checkout studio-master-dev`
- [Android Studio repo](https://android.googlesource.com/platform/tools/studio/google/cloud/testing/+/studio-master-dev/firebase-testing/lib)

## Build

- `gradle build`
