# Flank [![Build Status](https://app.bitrise.io/app/9767f3e19047d4db/status.svg?token=uDM3wCumR2xTd0axh4bjDQ&branch=master)](https://app.bitrise.io/app/9767f3e19047d4db) [![codecov](https://codecov.io/gh/TestArmada/flank/branch/master/graph/badge.svg)](https://codecov.io/gh/TestArmada/flank) [![pullreminders](https://pullreminders.com/badge.svg)](https://pullreminders.com?ref=badge)

Flank is a [massively parallel Android and iOS test runner](https://medium.com/walmartlabs/flank-smart-test-runner-for-firebase-cf65e1b1eca7) for [Firebase Test Lab](https://firebase.google.com/docs/test-lab/).

### Contributing

- Install [Oracle JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  - JDK 9 or later will not work
- Use [JetBrains Toolbox](https://www.jetbrains.com/toolbox/app/) to install `IntelliJ IDEA Community`
- Clone the repo `git clone --recursive https://github.com/TestArmada/flank.git`
  - `git submodule update --init --recursive` updates the submodules
- Open `test_runner/build.gradle.kts` with `IntelliJ IDEA Community`

### Features

Available now | 2019
 --           | --
Test sharding | Client/Server refactor
Cost reporting
Stability testing
HTML report
JUnit XML report
Smart Flank

### Exit Codes

Exit code | Description
 --       |         -- |
0         | All tests passed
1         | At least one test failed or inconclusive and all matrices finished.
2         | At least one matrix not finished, usually a FTL error.

### Flank configuration

app, test, and xctestrun-file support `~`, environment variables, and globs (*, **) when resolving paths

### iOS example

Run `test_runner/flank.ios.yml` with flank to verify iOS execution is working.

- `cd test_runner/`
- `./gradlew clean assemble fatJar`
- `java -jar ./build/libs/flank-*.jar firebase test ios run`

```yaml
# gcloud args match the official gcloud cli
# https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
gcloud:
  # -- GcloudYml --

  ## The name of a Google Cloud Storage bucket where raw test results will be stored
  # results-bucket: tmp_flank

  ## The name of a unique Google Cloud Storage object within the results bucket where raw test results will be stored
  ## (default: a timestamp with a random suffix).
  # results-dir: tmp

  ## Enable video recording during the test. Enabled by default, use --no-record-video to disable.
  # record-video: true

  ## The max time this test execution can run before it is cancelled (default: 15m).
  ## It does not include any time necessary to prepare and clean up the target device.
  ## The maximum possible testing time is 30m on physical devices and 60m on virtual devices.
  ## The TIMEOUT units can be h, m, or s. If no unit is given, seconds are assumed.
  # timeout: 30m

  ## Invoke a test asynchronously without waiting for test results.
  # async: false

  ## The billing enabled Google Cloud Platform project name to use
  # project: delta-essence-114723

  ## The history name for your test results (an arbitrary string label; default: the application's label from the APK manifest).
  ## All tests which use the same history name will have their results grouped together in the Firebase console in a time-ordered test history list.
  # results-history-name: android-history

  ## Experimental!
  ## The number of times a TestExecution should be re-attempted if one or more\nof its test cases fail for any reason.
  ## The maximum number of reruns allowed is 10. Default is 0, which implies no reruns.
  # flaky-test-attempts: 0

  # -- IosGcloudYml --

  ## The path to the test package (a zip file containing the iOS app and XCTest files).
  ## The given path may be in the local filesystem or in Google Cloud Storage using a URL beginning with gs://.
  ## Note: any .xctestrun file in this zip file will be ignored if --xctestrun-file is specified.
  test: ./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExample.zip

  ## The path to an .xctestrun file that will override any .xctestrun file contained in the --test package.
  ## Because the .xctestrun file contains environment variables along with test methods to run and/or ignore,
  ## this can be useful for customizing or sharding test suites. The given path should be in the local filesystem.
  ## Note: this path should usually be pointing to the xctestrun file within the derived data folder
  xctestrun-file: ./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleSwiftTests_iphoneos12.1-arm64e.xctestrun

  ## The version of Xcode that should be used to run an XCTest.
  ## Defaults to the latest Xcode version supported in Firebase Test Lab.
  ## This Xcode version must be supported by all iOS versions selected in the test matrix.
  # xcode-version: 10.1

  ## A list of DIMENSION=VALUE pairs which specify a target device to test against.
  ## This flag may be repeated to specify multiple devices.
  ## The four device dimensions are: model, version, locale, and orientation.
  # device:
  #  - model: iphone8
  #   version: 12.0
  #   locale: en
  #   orientation: portrait
  # - model: iphonex
  #   version: 12.0
  #   locale: es_ES
  #   orientation: landscape

flank:
  # -- FlankYml --

  ## test shards - the amount of groups to split the test suite into
  ## set to -1 to use one shard per test. default: 1
  # testShards: 1

  ## shard time - the amount of time tests within a shard should take
  ## default: -1 (unlimited)
  ## when testShards is -1 and shardTime is > 0, the shard count will
  ## be set dynamically based on time.
  # shardTime: -1

  ## repeat tests - the amount of times to run the tests.
  ## 1 runs the tests once. 10 runs all the tests 10x
  # repeatTests: 1

  ## Google cloud storage path to store the JUnit XML results from the last run.
  # smartFlankGcsPath: gs://tmp_flank/flank/test_app_ios.xml

  ## Disables sharding. Useful for parameterized tests.
  # disableSharding: false

  ## always run - these tests are inserted at the beginning of every shard
  ## useful if you need to grant permissions or login before other tests run
  # test-targets-always-run:
  #   - className/testName

  ## regex is matched against bucket paths, for example: 2019-01-09_00:18:07.314000_hCMY/shard_0/EarlGreyExampleSwiftTests_iphoneos12.1-arm64e.xctestrun
  # files-to-download:
  #   - .*\.mp4$

  # -- IosFlankYml --

  ## test targets - a list of tests to run. omit to run all tests.
  # test-targets:
  #   - className/testName
```

### Android example

Run `test_runner/flank.yml` with flank to verify Android execution is working.

- `cd test_runner/`
- `./gradlew clean assemble fatJar`
- `java -jar ./build/libs/flank-*.jar firebase test android run`

```yaml
# gcloud args match the official gcloud cli
# See the docs for full gcloud details https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run
gcloud:
  # -- GcloudYml --

  ## The name of a Google Cloud Storage bucket where raw test results will be stored
  # results-bucket: tmp_flank

  ## The name of a unique Google Cloud Storage object within the results bucket where raw test results will be stored
  ## (default: a timestamp with a random suffix).
  # results-dir: tmp

  ## Enable video recording during the test. Enabled by default, use --no-record-video to disable.
  # record-video: true

  ## The max time this test execution can run before it is cancelled (default: 15m).
  ## It does not include any time necessary to prepare and clean up the target device.
  ## The maximum possible testing time is 30m on physical devices and 60m on virtual devices.
  ## The TIMEOUT units can be h, m, or s. If no unit is given, seconds are assumed.
  # timeout: 30m

  ## Invoke a test asynchronously without waiting for test results.
  # async: false

  ## The billing enabled Google Cloud Platform project name to use
  # project: delta-essence-114723

  ## The history name for your test results (an arbitrary string label; default: the application's label from the APK manifest).
  ## All tests which use the same history name will have their results grouped together in the Firebase console in a time-ordered test history list.
  # results-history-name: android-history

  ## Experimental!
  ## The number of times a TestExecution should be re-attempted if one or more\nof its test cases fail for any reason.
  ## The maximum number of reruns allowed is 10. Default is 0, which implies no reruns.
  # flaky-test-attempts: 0

  # -- AndroidGcloudYml --

  ## The path to the application binary file.
  ## The path may be in the local filesystem or in Google Cloud Storage using gs:// notation.
  ## Android App Bundles are specified as .aab, all other files are assumed to be APKs.
  app: ../test_app/apks/app-debug.apk

  ## The path to the binary file containing instrumentation tests.
  ## The given path may be in the local filesystem or in Google Cloud Storage using a URL beginning with gs://.
  test: ../test_app/apks/app-debug-androidTest.apk

  ## Automatically log into the test device using a preconfigured Google account before beginning the test.
  ## Enabled by default, use --no-auto-google-login to disable.
  # auto-google-login: true

  ## Whether each test runs in its own Instrumentation instance with the Android Test Orchestrator
  ## (default: Orchestrator is used). Disable with --no-use-orchestrator.
  ## See https://developer.android.com/training/testing/junit-runner.html#using-android-test-orchestrator
  # use-orchestrator: true

  ## A comma-separated, key=value map of environment variables and their desired values. This flag is repeatable.
  ## The environment variables are mirrored as extra options to the am instrument -e KEY1 VALUE1 … command and
  ## passed to your test runner (typically AndroidJUnitRunner)
  # environment-variables:
  #  coverage: true
  #  coverageFilePath: /sdcard/
  #  clearPackageData: true

  ## A list of paths that will be copied from the device's storage to the designated results bucket after the test
  ## is complete. These must be absolute paths under /sdcard or /data/local/tmp
  # directories-to-pull:
  #   - /sdcard/

  ## Monitor and record performance metrics: CPU, memory, network usage, and FPS (game-loop only).
  ## Enabled by default, use --no-performance-metrics to disable.
  # performance-metrics: true

  ## A list of one or more test target filters to apply (default: run all test targets).
  ## Each target filter must be fully qualified with the package name, class name, or test annotation desired.
  ## Supported test filters by am instrument -e … include:
  ## class, notClass, size, annotation, notAnnotation, package, notPackage, testFile, notTestFile
  ## See https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner for more information.
  # test-targets:
  #  - class com.example.app.ExampleUiTest#testPasses

  ## A list of DIMENSION=VALUE pairs which specify a target device to test against.
  ## This flag may be repeated to specify multiple devices.
  ## The four device dimensions are: model, version, locale, and orientation.
  # device:
  # - model: NexusLowRes
  #   version: 28
  #   locale: en
  #   orientation: portrait
  # - model: NexusLowRes
  #   version: 27

flank:
  # -- FlankYml --

  ## test shards - the amount of groups to split the test suite into
  ## set to -1 to use one shard per test. default: 1
  # testShards: 1

  ## shard time - the amount of time tests within a shard should take
  ## default: -1 (unlimited)
  ## when testShards is -1 and shardTime is > 0, the shard count will
  ## be set dynamically based on time.
  # shardTime: -1

  ## repeat tests - the amount of times to run the tests.
  ## 1 runs the tests once. 10 runs all the tests 10x
  # repeatTests: 1

  ## Google cloud storage path to store the JUnit XML results from the last run.
  # smartFlankGcsPath: gs://tmp_flank/flank/test_app_android.xml

  ## Disables sharding. Useful for parameterized tests.
  # disableSharding: false

  ## always run - these tests are inserted at the beginning of every shard
  ## useful if you need to grant permissions or login before other tests run
  # test-targets-always-run:
  #   - class com.example.app.ExampleUiTest#testPasses

  ## regex is matched against bucket paths, for example: 2019-01-09_00:13:06.106000_YCKl/shard_0/NexusLowRes-28-en-portrait/bugreport.txt
  # files-to-download:
  #   - .*\.mp4$
```

### Android code coverage

<details>
<summary>Update your app's build.gradle to build with coverage and use orchestrator.
A custom gradle task is defined to generate the coverage report.</summary>

```gradle
def coverageEnabled = project.hasProperty('coverage')

android {

  defaultConfig {
    testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    // runs pm clear after each test invocation
    testInstrumentationRunnerArguments clearPackageData: 'true'
  }

  buildTypes {
    debug {
      testCoverageEnabled true
    }
  }
 
  // https://google.github.io/android-gradle-dsl/current/com.android.build.gradle.internal.dsl.TestOptions.html#com.android.build.gradle.internal.dsl.TestOptions:animationsDisabled
  testOptions {
        execution 'ANDROIDX_TEST_ORCHESTRATOR'
        animationsDisabled = true
    }
}

dependencies {
  androidTestUtil 'androidx.test:orchestrator:1.1.1'

  androidTestImplementation("androidx.test:runner:1.1.1")
  androidTestImplementation("androidx.test.ext:junit:1.1.0")
  androidTestImplementation("androidx.test.ext:junit-ktx:1.1.0")
  androidTestImplementation("androidx.test.ext:truth:1.1.0")
  androidTestImplementation("androidx.test.espresso.idling:idling-concurrent:3.1.1")
  androidTestImplementation("androidx.test.espresso.idling:idling-net:3.1.1")
  androidTestImplementation("androidx.test.espresso:espresso-accessibility:3.1.1")
  androidTestImplementation("androidx.test:rules:1.1.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1")
  androidTestImplementation("androidx.test.espresso:espresso-contrib:3.1.1")
  androidTestImplementation("androidx.test.espresso:espresso-idling-resource:3.1.1")
  androidTestImplementation("androidx.test.espresso:espresso-intents:3.1.1")
  androidTestImplementation("androidx.test.espresso:espresso-web:3.1.1")
}

if (coverageEnabled) {
    // gradle -Pcoverage firebaseJacoco
    task firebaseJacoco(type: JacocoReport) {
        group = "Reporting"
        description = "Generate Jacoco coverage reports for Firebase test lab."

        def excludes = [
                '**/R.class',
                '**/R$*.class',
                '**/BuildConfig.*',
                "**/androidx"]
        def javaClasses = fileTree(dir: "${project.buildDir}/intermediates/javac/debug/compileDebugJavaWithJavac/classes", excludes: excludes)
        def kotlinClasses = fileTree(dir: "${project.buildDir}/tmp/kotlin-classes/debug", excludes: excludes)
        getClassDirectories().setFrom(files([javaClasses, kotlinClasses]))

        getSourceDirectories().setFrom(files([
                'src/main/java', 'src/main/kotlin',
                'src/androidTest/java', 'src/androidTest/kotlin']))

        def ecFiles = project.fileTree(dir: '..', include: 'results/coverage_ec/**/sdcard/*.ec')
        ecFiles.forEach { println("Reading in $it") }
        getExecutionData().setFrom(ecFiles)

        reports {
            html { enabled true }
            xml { enabled false }
        }
    }
}
```

Here's an example flank.yml. Note that `coverage` and `coverageFilePath` must be set when using orchestrator with coverage.
`coverageFile` is not used. Orchestrator will generate one coverage file per test. `coverageFilePath` must be a directory, not a file.

```yaml
gcloud:
  app: ./app/build/outputs/apk/debug/app-debug.apk
  test: ./app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
  environment-variables: 
    coverage: true
    coverageFilePath: /sdcard/
    clearPackageData: true
  directories-to-pull: 
    - /sdcard/
  # use a named results dir that's used by the gradle task
  results-dir: coverage_ec

flank:
  disableSharding: true
  files-to-download:
    - .*/sdcard/[^/]+\.ec$
```

- Build the app with coverage: `./gradlew -Pcoverage build`
- Run flank `flank android run`
- Generate the report `./gradlew -Pcoverage firebaseJacoco`
- Open the report in `./build/reports/jacoco/firebaseJacoco/html/index.html`
</details>

### CI integration

Download Flank from GitHub releases.

Stable

```
wget --quiet https://github.com/TestArmada/flank/releases/download/v3.0.0/flank.jar -O ./flank.jar
java -jar ./flank.jar android run
```

Snapshot (published after every commit)

```
wget --quiet https://github.com/TestArmada/flank/releases/download/flank_snapshot/flank.jar -O ./flank.jar
java -jar ./flank.jar android run
```

In CI, it may be useful to generate the file via a shell script:

```
cat << 'EOF' > ./flank.yml
gcloud:
  app: ../../test_app/apks/app-debug.apk
  test: ../../test_app/apks/app-debug-androidTest.apk
EOF
```

### Gradle Plugin

[Fladle][fladle] is a Gradle plugin for Flank that provides DSL configuration and task based execution.

[fladle]: https://github.com/runningcode/fladle

### Authenticate with a Google account

Run `flank auth login`. Flank will save the credential to `~/.flank`. Google account authentication allows each person
to have a unique non-shared credential. A service account is still recommended for CI.

### Authenticate with a service account

Follow the [test lab docs](https://firebase.google.com/docs/test-lab/android/continuous) to create a service account.
- Save the credential to `$HOME/.config/gcloud/application_default_credentials.json` or set `GOOGLE_APPLICATION_CREDENTIALS` when using a custom path.
- Set the project id in flank.yml or set the `GOOGLE_CLOUD_PROJECT` environment variable.

For continuous integration, base64 encode the credential as `GCLOUD_KEY`. Then write the file using a shell script. Note that gcloud CLI does not need to be installed. Flank works without any dependency on gcloud CLI.

Encode JSON locally.

```bash
base64 -i "$HOME/.config/gcloud/application_default_credentials.json" | pbcopy
```

Then in CI decode the JSON.

```bash
GCLOUD_DIR="$HOME/.config/gcloud/"
mkdir -p "$GCLOUD_DIR"
echo "$GCLOUD_KEY" | base64 --decode > "$GCLOUD_DIR/application_default_credentials.json"
```

### Running with gcloud directly

flank.yml is compatible with the gcloud CLI.

- `gcloud firebase test android run flank.yml:gcloud`
- `gcloud alpha firebase test ios run flank.ios.yml:gcloud`

**NOTE:** You will need to [activate gcloud's service account](https://cloud.google.com/sdk/gcloud/reference/auth/activate-service-account) for the above commands to work.

### Doctor

Use the doctor command to check for errors in the YAML.

- `flank firebase test android doctor`
- `flank firebase test ios doctor`

### Check version

Flank supports printing the current version.

```bash
$ flank -v
v3.0-SNAPSHOT
```

### FAQ

> Access Not Configured. Cloud Tool Results API has not been used in project 764086051850 before or it is disabled.

This error means authentication hasn't been setup properly. See `Authenticate with a service account` in this readme.

> How do I use Flank without typing long commands?

Add Flank's [bash helper folder](https://github.com/TestArmada/flank/blob/master/test_runner/bash/) to your $PATH environment variable. This will allow you to call the shell scripts in that helper folder from anywhere.

With the [flank](https://github.com/TestArmada/flank/blob/master/test_runner/bash/flank) shell script, you can use `flank` instead of `java -jar flank.jar`. Examples:

- `flank android run`
- `flank ios run`

With the [update_flank.sh](https://github.com/TestArmada/flank/blob/master/test_runner/bash/update_flank.sh) shell script, you can rebuild `flank.jar`.

> Symbol is declared in module 'java.xml' which does not export package 'com.sun.org.apache.xerces.internal.dom'

Make sure you're using JDK 8 to compile Flank.

> Test run failed to complete. Expected 786 tests, received 660

Try setting `use-orchestrator: false`. Parameterized tests [are not compatible with orchestrator](https://stackoverflow.com/questions/48735268/unable-to-run-parameterized-tests-with-android-test-orchestrator). Flank uses [orchestrator by default on Android.](https://developer.android.com/training/testing/junit-runner)
