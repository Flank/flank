# Investigate flank commands

## List of commands android

### gcloud

1. app
2. test
3. additional-apks
4. auto-google-login
5. no-auto-google-login
6. use-orchestrator
7. no-use-orchestrator
8. environment-variables
9. directories-to-pull
10. other-files
11. performance-metrics
12. no-performance-metrics
13. num-uniform-shards
14. test-runner-class
15. test-targets
16. robo-directives
17. robo-script
18. results-bucket
19. results-dir
20. record-video
21. no-record-video
22. timeout
23. async
24. client-details
25. network-profile
26. results-history-name
27. num-flaky-test-attempts
28. device

### flank

1. additional-app-test-apks
2. legacy-junit-result
3. max-test-shards
4. shard-time
5. num-test-runs
6. smart-flank-gcs-path
7. smart-flank-disable-upload
8. disable-sharding
9. test-targets-always-run
10. files-to-download
11. project
12. local-result-dir
13. run-timeout
14. full-junit-result
15. ignore-failed-tests
16. keep-file-path
17. output-style
18. disable-results-upload
19. default-test-time
20. default-class-test-time
21. use-average-test-time-for-new-tests

## Investigation report

### environment-variables

Need to set ```directories-to-pull``` to pull from device directory with coverage report. 
There are no warnings or fails when ```environment-variables``` set without ```directories-to-pull```.

### files-to-download

In case when we want download coverage report we need to set ```directories-to-pull```.
There are no warnings or fails when ```files-to-download``` set without ```directories-to-pull```.

### disable-sharding

Can be set to true with set ```max-test-shards``` higher than one. In this case flank disable sharding. 

### num-uniform-shards

1. When set with ```max-test-shards``` Flank fast fail
2. When set with ```disable-sharding``` Flank disable sharding without any warning

## List of commands ios

### gcloud

1. test
2. xctestrun-file
3. xcode-version
4. results-bucket
5. results-dir
6. record-video
7. no-record-video
8. timeout
9. async
10. client-details
11. network-profile
12. results-history-name
13. num-flaky-test-attempts
14. device

### flank

1. test-targets
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
