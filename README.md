# Flank [![Build Status](https://travis-ci.org/TestArmada/flank.svg?branch=master)](https://travis-ci.org/TestArmada/flank) [![](https://jitpack.io/v/TestArmada/flank.svg)](https://jitpack.io/#TestArmada/flank)

Flank is a [Firebase Test Lab](https://firebase.google.com/docs/test-lab/?gclid=CjwKEAiA0fnFBRC6g8rgmICvrw0SJADx1_zAFTUPL4ffVSc5srwKT_Up4vJb15Ik4iIxIK4bQ5J-vxoCIS3w_wcB) tool for massively-scaling your automated Android tests. Run large test suites across many devices/versions/configurations at the same time, in parallel. Flank can easily be used in a CI environment where Gradle (or similar) first builds the APK:s and then Flank is used to execute the tests.

To use Flank, please sign up for Firebase Test Lab and install the Google Cloud SDK.

### Setup

* Install the [Java SDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

* Signup for [Firebase Test Lab](https://firebase.google.com/)

* Install the [Google Cloud SDK](https://cloud.google.com/sdk/docs/) (>= 176.0.0)

### Download

Either [download Flank from here](https://jitpack.io/com/github/TestArmada/flank/v2.0.2/flank-v2.0.2.jar)

or

Use curl:

```console
curl --location --fail https://jitpack.io/com/github/TestArmada/flank/v2.0.2/flank-v2.0.2.jar --output Flank-2.0.2.jar
```

### Run Tests

To runs tests with Flank you will need the app and test apk's. You can specify in which class/package/annotation/testFile you would like tests to run by using ```-f```. If ```-f``` is not provided all the tests will be executed. Usage:

```
java -jar Flank-2.0.2.jar -a app-apk -t test-apk -f "class/package/annotation/testFile name"

"class foo.FooTest, bar.BarTest" executes only the tests in the given class(es)
"class foo.FooTest#testFoo, bar.BarTest#testBar" executes only the given test(s)
"package foo, bar" executes only the tests in the given package(s)
"annotation foo.MyAnnotation, bar.MyAnnotation" executes only the tests annotated with the given annotation(s)
"testFile path/to/file" executes only the tests listed in the given file. The file should contain a list of line separated package names or test classes and optionally methods

To invert the operator add the 'not' prefix. For example, 'notPackage' executes all the tests except the ones in the given package.
```

When the executions are completed Flanks will fetch the xml result files, add device name to the tests and store the files in a folder named: ```results```.

### Configure Flank

It's possible to configure Flank by including a Java properties file in the root folder: ```config.properties```

Following properties can be configured:

```
devices: Device Id, OSVersion, orientation, locale; Default values are `Nexus6P`, `25`, `portrait` and `en`
environment-variables: To set environment variables. Can also be used to enable code coverage
directories-to-pull: If directories from the device should be pulled
use-orchestrator: To enable Android Test Orchestrator
shard-timeout: Timeout in minutes for each shard. 5 minutes by default.
shard-duration: Duration in seconds for each shard

numShards: Number of shards to split the tests across. Unused by default.
shardIndex: If a specific shard should be executed
debug-prints: If debug prints should be enabled. False by default.
fetch-xml-files: If the result xml files should be fetched. True by default.
fetch-bucket: If the bucket containing logs and artifacts should be fetched. False by default.
gcloud-path: The path to the glcoud binary. This shouldn't need to be set if `gcloud` is found on the path.
gsutil-path: The path to the gsutil binary. This shouldn't need to be set if `gcloud` is found on the path.
gcloud-bucket: The Google Cloud Storage bucket to use. If not specified Flank will create one.
use-gcloud-beta: If gcloud beta should be used. False by default.
auto-google-login: Automatically log into the test device using a preconfigured Google account before beginning the test. True by default.
record-video: If the video of the instrumentation test should be recorded. True by default.
record-performance-metrics: If the performance metrics should be recorded. True by default.

aggregate-reports.enabled: Enable the experimental test aggregation feature. Requires fetch-bucket to be enabled. False by default.
aggregate-reports.xml: Generates and pushes to Google Cloud Storage an aggregated XML report. True by default.
aggregate-reports.html: Generates and pushes to Google Cloud Storage an aggregated HTML report, including Logcat and video for the failing tests. True by default.
aggregate-reports.split-video: For failing test cases, attempts to split the execution video to capture only the failing test and includes a link in the HTML report. Requires ffmpeg to be installed. False by default.

deviceIds: The ID:s of the devices to run the tests on (deprecated, should not be used in conjunction with `device`)
os-version-ids: The API-levels of the devices to run the tests on (deprecated, should not be used in conjunction with `device`)
orientations: The orientations, portrait, landscape or both (deprecated, should not be used in conjunction with `device`)
locales: The device locales (deprecated, should not be used in conjunction with `device`)
```

Example of a properties file:

```
environment-variables=coverage=true,coverageFile=/sdcard/tempDir/coverage.ec
directories-to-pull=/sdcard
shard-timeout=5
shard-duration=120
devices=model:Nexus5X;version:23;orientation:portrait;locale:en,\
        model:Nexus6P;version:24,\
        model:NexusLowRes;version:26

numShards=
shardIndex=
debug-prints=false
fetch-xml-files=true
auto-google-login=true
fetch-bucket=false
gcloud-path=
gsutil-path=
```

### Configurable Shards

Flank supports configurable shard durations. Instead of creating one shard per test case Flank can create shards that contain tests that add up to a given duration. This feature was introduced to make Flank faster while also saving cost (seconds are round up to minutes by Firebase test lab). By default Flank will try to create shards with tests adding up to 2 minutes in execution time.

First time a new app is tested each test is run in its own shard so that Flank can save the individual execution times. This is also the case for new tests that are introduced. The execution times for tests are saved in a file: ```flank.tests```. This file is used by Flank to create shards and is backed up in your google cloud bucket. Open the file in an editor to edit the execution times. Create an empty ```flank.tests``` in the root Flank folder to start from a clean slate.

```shard-duration``` can be set in ```config.properties```. Default shard duration is 120 seconds. To enable one test per shard set ```shard-duration``` to -1 or specify a custom number of shards with ```numShards```.

### Troubleshooting

Please enable debug mode ```debug-prints:true``` in the properties file if it's not working correctly.

If Flank hangs and nothing seems to be happening (even when debug mode is enabled) make sure the correct user (registered with Firebase) is logged into Firebase. Please also see [Google Cloud Storage Authentication](https://cloud.google.com/storage/docs/authentication).

If you see `AccessDeniedException: 403` verify that:
- The GCloud SDK is on the latest version
- The Firebase project is on a paid subscription (for free accounts specify the default bucket with property: `gcloud-bucket`)
- The Firebase account and project ID are in sync

Follow below steps to set it:

```
gcloud components update - Update the GCloud SDK

gcloud config list - To view gcloud SDK properties.

gcloud auth login - To set gcloud to the correct google account.

gcloud config set project <idOfYourProject> - To set gcloud to the correct project ID.

```

If you are using the terminal in OS X and Flank gets stuck at around 250 shards its because of a setting in OS X. To fix it [please follow these instructions](https://blog.dekstroza.io/ulimit-shenanigans-on-osx-el-capitan/).
