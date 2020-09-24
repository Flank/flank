# Investigate flank options

## List of options android

### gcloud

0. app
0. test
0. additional-apks
0. auto-google-login
0. no-auto-google-login
0. use-orchestrator
0. no-use-orchestrator
0. environment-variables
0. directories-to-pull
0. other-files
0. performance-metrics
0. no-performance-metrics
0. num-uniform-shards
0. test-runner-class
0. test-targets
0. robo-directives
0. robo-script
0. results-bucket
0. results-dir
0. record-video
0. no-record-video
0. timeout
0. async
0. client-details
0. network-profile
0. results-history-name
0. num-flaky-test-attempts
0. device

### flank

0. additional-app-test-apks
0. legacy-junit-result
0. max-test-shards
0. shard-time
0. num-test-runs
0. smart-flank-gcs-path
0. smart-flank-disable-upload
0. disable-sharding
0. test-targets-always-run
0. files-to-download
0. project
0. local-result-dir
0. run-timeout
0. full-junit-result
0. ignore-failed-tests
0. keep-file-path
0. output-style
0. disable-results-upload
0. default-test-time
0. default-class-test-time
0. use-average-test-time-for-new-tests

## List of options ios

### gcloud

0. test
0. xctestrun-file
0. xcode-version
0. results-bucket
0. results-dir
0. record-video
0. no-record-video
0. timeout
0. async
0. client-details
0. network-profile
0. results-history-name
0. num-flaky-test-attempts
0. device

### flank

0. test-targets
0. max-test-shards
0. shard-time
0. num-test-runs
0. smart-flank-gcs-path
0. smart-flank-disable-upload
0. disable-sharding
0. test-targets-always-run
0. files-to-download
0. project
0. local-result-dir
0. run-timeout
0. full-junit-result
0. ignore-failed-tests
0. keep-file-path
0. output-style
0. disable-results-upload
0. default-test-time
0. default-class-test-time
0. use-average-test-time-for-new-tests

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

0. When set with ```max-test-shards``` Flank will fail fast.
0. When set with ```disable-sharding```, Flank will disable sharding without any warning
   - Warning added about this.
