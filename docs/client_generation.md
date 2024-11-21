# Overview

The `google-api-*-client` projects are officially marked as `maintenance mode`. They don't have a consistent code generation approach. Each binding appears to do something different. [`apis-client-generator`](https://github.com/google/apis-client-generator) is used to generate REST bindings.

The new clients are `google-cloud-*`. They are in the process of adopting the latest code generation tech `googleapis/toolkit` which is gRPC based.

> These handwritten packages are going to migrate to live on top of clients generated from the Google API Code Generator
>
> https://github.com/GoogleCloudPlatform/google-cloud-go/issues/266#issuecomment-221083266

# API client summary

Google API clients (`google-api-*-client`)
- Low level
- Auto generated
- Made for JSON REST APIs
- Older project - [maintenance mode](https://github.com/GoogleCloudPlatform/google-cloud-ruby/issues/1604#issuecomment-316834237)

Google APIs toolkit (`googleapis/toolkit`)
- Low level
- Auto generated
- Made for gRPC APIs
- New project - actively developed

Google Cloud libraries (`google-cloud-*`)
- High level
- Hand written
- [Built on top of the low level clients](https://github.com/GoogleCloudPlatform/google-cloud-ruby/issues/1604#issuecomment-316841696)

# google-api-client - `maintenance mode`

- [google-api-nodejs-client](https://github.com/google/google-api-nodejs-client)
- [google-api-php-client](https://github.com/google/google-api-php-client)
- [google-api-python-client](https://github.com/google/google-api-python-client)
- [google-api-ruby-client](https://github.com/google/google-api-ruby-client)
- [google-api-go-client](https://github.com/google/google-api-go-client)
  - [google-api-go-generator](https://github.com/google/google-api-go-client/tree/master/google-api-go-generator)
- [google-api-java-client](https://github.com/google/google-api-java-client)
- [google-api-javascript-client](https://github.com/google/google-api-javascript-client) - not officially [marked as maintenance,](https://github.com/google/google-api-javascript-client/issues/320) however no new updates since May 2017.
- [google-api-dotnet-client](https://github.com/google/google-api-dotnet-client)
- [google-api-objectivec-client](https://github.com/google/google-api-objectivec-client)
  - Deprecated. Replaced entirely by [google-api-objectivec-client-for-rest](https://github.com/google/google-api-objectivec-client-for-rest)
- [google-api-cpp-client](https://github.com/google/google-api-cpp-client)

# apis-client-generator - `active`

- [apis-client-generator](https://github.com/google/apis-client-generator)
  - Java, C++, C#, GWT, PHP, Dart
  - Used to generate `google-api-java-client`
  - [Recommended way to generate bindings for REST APIs](https://github.com/google/apis-client-generator/issues/31#issuecomment-323881910)

# apitools - `maintenance mode`

- [apitools](https://github.com/google/apitools)
  - Generates Python only. Used by gcloud CLI
  - Writes protobuf files from API discovery

# google-cloud - `active`

Actively developed. Google Cloud APIs only.

- [google-cloud-ruby](https://github.com/GoogleCloudPlatform/google-cloud-ruby)
- [google-cloud-node](https://github.com/GoogleCloudPlatform/google-cloud-node)
- [google-cloud-python](https://github.com/GoogleCloudPlatform/google-cloud-python)
- [google-cloud-go](https://github.com/GoogleCloudPlatform/google-cloud-go)
- [google-cloud-java](https://github.com/GoogleCloudPlatform/google-cloud-java)
- [google-cloud-php](https://github.com/GoogleCloudPlatform/google-cloud-php)
- [google-cloud-dotnet](https://github.com/GoogleCloudPlatform/google-cloud-dotnet)

# googleapis/toolkit - `active`

The latest code generation tech from Google.

- [googleapis/toolkit](https://github.com/googleapis/toolkit)
- [googleapis/api-compiler](https://github.com/googleapis/api-compiler)

---

# Generating Java APIs with `apis-client-generator`

Install Google API client generator from source.

```
git clone https://github.com/google/apis-client-generator.git
pip install .
```

Generate the library manually:

```
 generate_library \
    --input=./testing_v1.json \
    --language=java \
    --output_dir=./testing
```

Alternatively, generate the library by running `generate.sh`
