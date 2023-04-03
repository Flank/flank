# Gcloud output

## Android

### Welcome section

```bash
Have questions, feedback, or issues? Get support by visiting:
  https://firebase.google.com/support/
```


### Upload info

```bash
Uploading [../test_projects/android/apks/app-debug.apk] to Firebase Test Lab...
Uploading [../test_projects/android/apks/app-debug-androidTest.apk] to Firebase Test Lab...
```

### Storage information

```bash
Raw results will be stored in your GCS bucket at [https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2021-02-28_19:08:02.351035_LadZ/]
```

### Info before running test

```
Test [matrix-1uohkjt5rsr28] has been created in the Google Cloud.
Firebase Test Lab will execute your instrumentation test on 1 device(s).
Creating individual test executions...done.   
```

### Test status

```bash
Test results will be streamed to [https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.bf178d418be9d33e/matrices/4823249401837879607].
19:08:18 Test is Pending
19:08:46 Starting attempt 1.
19:08:46 Started logcat recording.
19:08:46 Test is Running
19:08:53 Started crash monitoring.
19:08:53 Preparing device.
19:09:00 Logging in to Google account on device.
19:09:06 Installing apps.
19:09:06 Retrieving Pre-Test Package Stats information from the device.
19:09:06 Retrieving Performance Environment information from the device.
19:09:06 Started crash detection.
19:09:06 Started Out of memory detection
19:09:06 Started performance monitoring.
19:09:06 Started video recording.
19:09:06 Starting instrumentation test.
19:09:06 Completed instrumentation test.
19:09:13 Stopped performance monitoring.
19:09:13 Retrieving Post-test Package Stats information from the device.
19:09:13 Logging out of Google account on device.
19:09:20 Stopped crash monitoring.
19:09:20 Stopped logcat recording.
19:09:40 Done. Test time = 1 (secs)
19:09:40 Starting results processing. Attempt: 1
19:09:47 Completed results processing. Time taken = 4 (secs)
19:09:47 Test is Finished
```

### Completion info

```
Instrumentation testing complete.
```



### Results

```bash
More details are available at [https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.bf178d418be9d33e/matrices/4823249401837879607].
┌─────────┬────────────────────────┬─────────────────────┐
│ OUTCOME │    TEST_AXIS_VALUE     │     TEST_DETAILS    │
├─────────┼────────────────────────┼─────────────────────┤
│ Passed  │ walleye-27-en-portrait │ 1 test cases passed │
└─────────┴────────────────────────┴─────────────────────┘
```



### Survey at the end

```
To take a quick anonymous survey, run:
  $ gcloud survey
```



## iOS

### Welcome section

```bash
Have questions, feedback, or issues? Get support by emailing:
  ftl-ios-feedback@google.com
```


### Upload info

```bash
Uploading [./src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExample.zip] to Firebase Test Lab...
Uploading [./src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExampleSwiftTests.xctestrun] to Firebase Test Lab...
Raw results will be stored in your GCS bucket at [https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2021-02-28_19:15:20.392870_nuvf/]
```

### Storage information

```bash
Test [matrix-2k6ldtp789lvy] has been created in the Google Cloud.
Firebase Test Lab will execute your xctest test on 1 device(s).
Creating individual test executions...done. 
```

### Info before running test

```
Test [matrix-2k6ldtp789lvy] has been created in the Google Cloud.
Firebase Test Lab will execute your xctest test on 1 device(s).
Creating individual test executions...done.   
```

### Test status

```bash
Test results will be streamed to [https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.a3b607c9bb6d0088/matrices/7622498233210161623].
19:15:49 Test is Pending
19:16:44 Starting attempt 1.
19:16:44 Checking Internet connection...
19:16:44 Test is Running
19:18:22 Internet connection stable!
19:18:41 Started device logs task
19:20:20 Stopped device logs task
19:20:39 Done. Test time = 62 (secs)
19:20:39 Starting results processing. Attempt: 1
19:20:45 Completed results processing. Time taken = 5 (secs)
19:20:45 Test is Finished
```

### Completion info

```
Xctest testing complete.
```



### Results

```bash
More details are available at [https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.a3b607c9bb6d0088/matrices/7622498233210161623].
┌─────────┬──────────────────────────┬──────────────────────┐
│ OUTCOME │     TEST_AXIS_VALUE      │     TEST_DETAILS     │
├─────────┼──────────────────────────┼──────────────────────┤
│ Passed  │ iphone13pro-15.2-en-portrait │ 17 test cases passed │
└─────────┴──────────────────────────┴──────────────────────┘
```

