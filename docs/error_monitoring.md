# Flank Error Monitoring

Flank uses Sentry to monitor test runner stability. Sentry enables data driven decisions when prioritizing bug fixes.

- https://sentry.io/
- https://docs.sentry.io/

## Data Captured

Sentry captures the following error data:

Flank
  - Stacktrace
  - releaseStage of Flank (production or snapshot)
  - version of Flank (git commit Flank was built from)

Device
  - hostname
  - locale
  - osArch
  - osName
  - osVersion
  - runtimeVersions of Java

## Disable Sentry

Flank respects the same analytics opt out as gcloud CLI.

`echo "DISABLED" > ~/.gsutil/analytics-uuid`

## More information

To see how Sentry is integrated within the Flank project please see the 
[Flank Sentry testcase](../test_runner/src/test/kotlin/ftl/util/FlankSentryInitHelperTest.kt) and 
[the actual Sentry implementation](../test_runner/src/main/kotlin/ftl/util/CrashReporter.kt)

- [Sentry Data Security, Privacy, and Compliance Overview](https://sentry.io/security/)
