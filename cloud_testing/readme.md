## Client libraries

Google provides [two types of client libraries:](https://cloud.google.com/apis/docs/client-libraries-explained) `Google API Client Libraries` and `Google Cloud Client Libraries`.

- `google-api-services-*` - legacy
  - `google-api-services-storage`
  - `google-api-services-toolresults`
  - `google-api-services-testing` - source code not published
- `google-cloud-*` - newest
  - `google-cloud-storage`

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

[Apache licensed](https://android.googlesource.com/platform/tools/studio/google/cloud/testing/+/studio-master-dev/firebase-testing/lib/licenses/google-api-services-testing.jar-NOTICE) `google-api-services-testing` extracted from [the open source Android Studio repo.](https://android.googlesource.com/platform/tools/studio/google/cloud/testing/+/studio-master-dev/firebase-testing/lib) Not published to maven central.

## Clone
- `git clone https://android.googlesource.com/platform/tools/studio/google/cloud/testing`
- `git checkout studio-master-dev`
- [Android Studio repo](https://android.googlesource.com/platform/tools/studio/google/cloud/testing/+/studio-master-dev/firebase-testing/lib)

## Decompile

[Fernflower](https://github.com/fesh0r/fernflower) is the preferred decompiler because it's open source and used by IntelliJ. [Cfr](http://www.benf.org/other/cfr/) is available for comparison.

- `gradle --rerun-tasks decompileFern`
- `gradle --rerun-tasks decompileCfr`

## Build

- `gradle build`
