# Fetch build metrics

In this Kotlin example, the app listens to new build events and fetches data

## Concepts

- Build streaming
- Retrieving build events for a build

## Setup

To run this sample:

1. Replace `id`, `password`, `baseUrl` values in [`FetchBuildData.kt`][FetchBuildData] with your Gradle Enterprise hostname and credentials.
2. Open a terminal window.
3. Run `./gradlew run` from the command line.

Sample output:
```
> Task :run
Start listening to build events

onOpen Response{protocol=http/1.1, code=200, message=OK, url=https://enterprise-training.gradle.com/build-export/v1/builds/since/now?stream}
```

[FetchBuildData]: src/main/kotlin/ftl/sample/FetchBuildData.kt
