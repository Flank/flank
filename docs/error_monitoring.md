# Flank Error Monitoring

Flank uses Bugsnag to monitor test runner stability. Bugsnag provides diagnostic data that is used to make data driven decisions when prioritizing fixing bugs or adding new features.

- https://www.bugsnag.com/
- https://docs.bugsnag.com/

## Data Captured

Bugsnag captures the following error data:

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

## Disable Bugsnag

Flank respects the same analytics opt out as gcloud CLI.

`echo "DISABLED" > ~/.gsutil/analytics-uuid`

 ## More information

To see how Bugsnag is integrated within the Flank project please see the Flank Bugsnag testcase [here](../test_runner/src/test/kotlin/ftl/util/FlankBugsnagInitHelperTest.kt) and the actual Bugsnag implementation found [here](../test_runner/src/main/kotlin/ftl/util/BugsnagInitHelper.kt)

More information about Bugsnag data policy can be found [here](https://docs.bugsnag.com/legal/privacy-policy/#:~:text=Services%20via%20Mobile%20Devices.&text=Bugsnag%20will%20collect%20certain%20information,operating%20system%20of%20your%20device.)
