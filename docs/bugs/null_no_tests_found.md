# The problem
One or more shards failed because there are no test cases inside. This problem encounter a couple times on multi-module tests which use a lot of additional test apks. It was noticed only with the submodule tests.

## Stack trace
```
java.lang.ClassNotFoundException: Invalid name: no tests found
at java.lang.Class.classForName(Native Method)
at java.lang.Class.forName(Class.java:324)
at androidx.test.internal.runner.TestLoader.doCreateRunner(TestLoader.java:72)
at androidx.test.internal.runner.TestLoader.getRunnersFor(TestLoader.java:104)
at androidx.test.internal.runner.TestRequestBuilder.build(TestRequestBuilder.java:793)
at androidx.test.runner.AndroidJUnitRunner.buildRequest(AndroidJUnitRunner.java:547)
at androidx.test.runner.AndroidJUnitRunner.onStart(AndroidJUnitRunner.java:390)
at com.work.android.test.view.ViewTestRunner.onStart(ViewTestRunner.kt:25)
at android.app.Instrumentation$InstrumentationThread.run(Instrumentation.java:1879)
```

## Firebase screens
![firebase1](https://user-images.githubusercontent.com/1173057/83044337-e4f21e80-9ff8-11ea-908a-b742f0431ab8.png)
![firebase2](https://user-images.githubusercontent.com/74583/83170210-0a4e5d80-a0c9-11ea-8011-08513ad281d8.png)
![firebase3](https://user-images.githubusercontent.com/74583/83170254-1b976a00-a0c9-11ea-8b7b-d689e9c086c1.png)
![firebase4](https://user-images.githubusercontent.com/74583/83170280-25b96880-a0c9-11ea-8296-12d7c9ad28cf.png)

## References
* github issue https://github.com/Flank/flank/issues/818
* reported for [Flank v20.05.2](https://github.com/Flank/flank/releases/tag/v20.05.2)

## Reported flank config
```yaml
flank:
  additional-app-test-apks:
  - test: feature/1/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/2/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/3/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/4/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/5/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/6/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/7/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/8/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/9/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/10/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/11/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/12/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/13/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/14/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/15/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/16/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/17/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/18/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/19/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/20/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/21/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/22/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/23/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/24/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/25/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/26/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  - test: feature/27/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  legacy-junit-result: 'false'
  local-result-dir: sub-modules-flank-results
  max-test-shards: 5
  project: project-id-12345
  run-timeout: 20m
  smart-flank-gcs-path: gs://project-id-12345-flank/submodules-timing.xml
gcloud:
  app: ./sample-app/build/outputs/apk/debug/sample-app-tester-debug.apk
  async: false
  auto-google-login: false
  device:
  - model: NexusLowRes
    version: 23
  environment-variables:
    clearPackageData: true
  performance-metrics: false
  record-video: false
  results-bucket: sub-modules-flank-results
  test: feature/28/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
  test-targets:
  - notAnnotation org.junit.Ignore
  use-orchestrator: true
```

## Reported fladle config
```groovy
fladle {
    serviceAccountCredentials = file("pathToCredentials")
    useOrchestrator = true
    environmentVariables = [
        "clearPackageData"     : "true"
    ]
    timeoutMin = 30
    recordVideo = true
    performanceMetrics = false
    devices = [
        ["model": "Pixel2", "version": "27", "orientation": "portrait", "locale": "en"]
    ]
    projectId = "id"
    flankVersion = "20.05.2"
    testShards = 5
    flakyTestAttempts = 0
}
```

## What we know from report issue
1. Firebase [shows error](https://user-images.githubusercontent.com/74583/83170254-1b976a00-a0c9-11ea-8b7b-d689e9c086c1.png) `null` `no tests found`
2. Same error occurs with and without `test-targets: - notAnnotation org.junit.Ignore`
3. With `test-targets: - notAnnotation org.junit.Ignore` firebase additionally shows `One or more shards failed because there are no test cases inside. Please check your sharding configuration.`
4. Occurs with custom annotation-based filter.
5. It was noticed only with the multi-module tests.
6. If number of `shards > 2`, there is a high chance one of the shards will have no tests.
7. `test-targets-always-run` is not the case.
8. Duplicated apk names probably are not the case, but we should ensure.

## How to reproduce it
* Use `additional-test-app-apk` option and set different apks with same names from different directories. Apks will overlap on bucket because of names collision. This should give similar result to reported.  

## Proposals
* ~~Drop ignored tests before shard calculation and use them only for results. https://github.com/Flank/flank/pull/853~~
* ~~Apks uploaded to bucket could overlap if has same names, fixing this could help. https://github.com/Flank/flank/pull/854~~
* Create multi-module project which will provide many test apks and try to reproduce issue.
* Ensure that our knowledge about issue in `What we know from report issue` is correct.
* Based on: https://stackoverflow.com/questions/39241640/android-instrumented-test-no-tests-found try to make real multi dex app and try trun tests on it
* Play with @BeforeClass and @Before annotations (exception on @BeforeClass produce null without test cases on test-lab).
