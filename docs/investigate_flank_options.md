# Investigate flank options

## List of options android

### gcloud

0. app
1. test
2. additional-apks
3. auto-google-login
4. no-auto-google-login
5. use-orchestrator
6. no-use-orchestrator
7. environment-variables
8. directories-to-pull
9. other-files
10. performance-metrics
11. no-performance-metrics
12. num-uniform-shards
13. test-runner-class
14. test-targets
15. robo-directives
16. robo-script
17. results-bucket
18. results-dir
19. record-video
20. no-record-video
21. timeout
22. async
23. client-details
24. network-profile
25. results-history-name
26. num-flaky-test-attempts
27. device

### flank

0. additional-app-test-apks
1. legacy-junit-result
2. max-test-shards
3. shard-time
4. num-test-runs
5. smart-flank-gcs-path
6. smart-flank-disable-upload
7. disable-sharding
8. test-targets-always-run
9. files-to-download
10. project
11. local-result-dir
12. run-timeout
13. full-junit-result
14. ignore-failed-tests
15. keep-file-path
16. output-style
17. disable-results-upload
18. default-test-time
19. default-class-test-time
20. use-average-test-time-for-new-tests

## List of options ios

### gcloud

0. test
1. xctestrun-file
2. xcode-version
3. results-bucket
4. results-dir
5. record-video
6. no-record-video
7. timeout
8. async
9. client-details
10. network-profile
11. results-history-name
12. num-flaky-test-attempts
13. device

### flank

0. test-targets
1. max-test-shards
2. shard-time
3. num-test-runs
4. smart-flank-gcs-path
5. smart-flank-disable-upload
6. disable-sharding
7. test-targets-always-run
8. files-to-download
9. project
10. local-result-dir
11. run-timeout
12. full-junit-result
13. ignore-failed-tests
14. keep-file-path
15. output-style
16. disable-results-upload
17. default-test-time
18. default-class-test-time
19. use-average-test-time-for-new-tests

## Investigation report

### environment-variables (Android)

Need to set ```directories-to-pull``` to pull from device directory with coverage report.
There are no warnings or fails when ```environment-variables``` set without ```directories-to-pull```.
Added warning.

### files-to-download (Android)

In the case where coverage reports need to be downloaded set the ```directories-to-pull``` variable.
There are no warnings or fails when ```files-to-download``` set without ```directories-to-pull```.
Added warning.

### disable-sharding (Common)

Can be enabled by setting ```max-test-shards``` to greater than one. In this case flank will disable sharding
Added warning.

### num-uniform-shards (Android)

0. When set with ```max-test-shards``` Flank will fail fast.
1. When set with ```disable-sharding```, Flank will disable sharding without any warning
   - Warning added about this.
