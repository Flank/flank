# Investigate flank options

## List of options android

### gcloud

1. app
1. test
1. additional-apks
1. auto-google-login
1. no-auto-google-login
1. use-orchestrator
1. no-use-orchestrator
1. environment-variables
1. directories-to-pull
1. other-files
1. performance-metrics
1. no-performance-metrics
1. num-uniform-shards
1. test-runner-class
1. test-targets
1. robo-directives
1. robo-script
1. results-bucket
1. results-dir
1. record-video
1. no-record-video
1. timeout
1. async
1. client-details
1. network-profile
1. results-history-name
1. num-flaky-test-attempts
1. device

### flank

1. additional-app-test-apks
1. legacy-junit-result
1. max-test-shards
1. shard-time
1. num-test-runs
1. smart-flank-gcs-path
1. smart-flank-disable-upload
1. disable-sharding
1. test-targets-always-run
1. files-to-download
1. project
1. local-result-dir
1. run-timeout
1. full-junit-result
1. ignore-failed-tests
1. keep-file-path
1. output-style
1. disable-results-upload
1. default-test-time
1. default-class-test-time
1. use-average-test-time-for-new-tests

## List of options ios

### gcloud

1. test
1. xctestrun-file
1. xcode-version
1. results-bucket
1. results-dir
1. record-video
1. no-record-video
1. timeout
1. async
1. client-details
1. network-profile
1. results-history-name
1. num-flaky-test-attempts
1. device

### flank

1. test-targets
1. max-test-shards
1. shard-time
1. num-test-runs
1. smart-flank-gcs-path
1. smart-flank-disable-upload
1. disable-sharding
1. test-targets-always-run
1. files-to-download
1. project
1. local-result-dir
1. run-timeout
1. full-junit-result
1. ignore-failed-tests
1. keep-file-path
1. output-style
1. disable-results-upload
1. default-test-time
1. default-class-test-time
1. use-average-test-time-for-new-tests

## Investigation report

### environment-variables (Android)

Set the ```directories-to-pull``` variable to pull from the device directory with coverage report.
There will be no warnings or failure messages when ```environment-variables``` is set without ```directories-to-pull```
A warning has been added about this.

### files-to-download (Android)

In the case where coverage reports need to be downloaded set the ```directories-to-pull``` variable.
There will be no warnings or failures when ```files-to-download``` is set without ```directories-to-pull```.
A warning is added regarding this.

### disable-sharding (Common)

Can be enabled by setting ```max-test-shards``` to greater than one. In this case flank will disable sharding
A warning is added regarding this.

### num-uniform-shards (Android)

1. When set with ```max-test-shards``` Flank will fail fast.
1. When set with ```disable-sharding```, Flank will disable sharding without any warning
   - Warning added about this.
