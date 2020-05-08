# Flank [![Build Status](https://app.bitrise.io/app/9767f3e19047d4db/status.svg?token=uDM3wCumR2xTd0axh4bjDQ&branch=master)](https://app.bitrise.io/app/9767f3e19047d4db) [![codecov](https://codecov.io/gh/Flank/flank/branch/master/graph/badge.svg)](https://codecov.io/gh/Flank/flank) [![pullreminders](https://pullreminders.com/badge.svg)](https://pullreminders.com?ref=badge)

Flank is a [massively parallel Android and iOS test runner](https://docs.google.com/presentation/d/1goan9cXpimSJsS3L60WjljnFA_seUyaWb2e-bezm084/edit#slide=id.p1) for [Firebase Test Lab](https://firebase.google.com/docs/test-lab/).

### Sponsors

[<img src="./docs/bugsnag-logo-dark.png" width="50%" />](https://www.bugsnag.com/)

### Contributing

- Install [Oracle JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  - JDK 9 or later will not work
- Use [JetBrains Toolbox](https://www.jetbrains.com/toolbox/app/) to install `IntelliJ IDEA Community`
- Clone the repo `git clone --recursive https://github.com/Flank/flank.git`
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
1         | All matrices finished but at least one test failed or inconclusive.
2         | Usually indicates missing or wrong usage of flags, incorrect parameters, errors in config files.
3         | At least one matrix not finished (usually a FTL internal error) or unexpected error occurred.

## CLI

Flank supports CLI flags for each YAML parameter. The CLI flags are useful to selectively override YAML file values. Pass the `--help` flag to see the full documentation. For example: `flank android run --help`

CLI flags work well with environment variables. You can override a value like this:

> flank android run --local-result-dir=$APP_NAME

### Flank configuration

app, test, and xctestrun-file support `~`, environment variables, and globs (*, **) when resolving paths

### iOS example

Run `test_runner/flank.ios.yml` with flank to verify iOS execution is working.

- `cd test_runner/`
- `./gradlew clean build shadowJar`
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

  ## Enable video recording during the test. Disabled by default. Use --record-video to enable.
  # record-video: true

  ## The max time this test execution can run before it is cancelled (default: 15m).
  ## It does not include any time necessary to prepare and clean up the target device.
  ## The maximum possible testing time is 30m on physical devices and 60m on virtual devices.
  ## The TIMEOUT units can be h, m, or s. If no unit is given, seconds are assumed.
  # timeout: 30m

  ## Invoke a test asynchronously without waiting for test results.
  # async: false

  ## A key-value map of additional details to attach to the test matrix.
  ## Arbitrary key-value pairs may be attached to a test matrix to provide additional context about the tests being run.
  ## When consuming the test results, such as in Cloud Functions or a CI system,
  ## these details can add additional context such as a link to the corresponding pull request.
  # client-details
  #   key1: value1
  #   key2: value2

  ## The name of the network traffic profile, for example LTE, HSPA, etc,
  ## which consists of a set of parameters to emulate network conditions when running the test
  ## (default: no network shaping; see available profiles listed by the `flank test network-profiles list` command).
  ## This feature only works on physical devices.
  # network-profile: LTE

  ## The history name for your test results (an arbitrary string label; default: the application's label from the APK manifest).
  ## All tests which use the same history name will have their results grouped together in the Firebase console in a time-ordered test history list.
  # results-history-name: android-history

  ## Experimental!
  ## The number of times a TestExecution should be re-attempted if one or more\nof its test cases fail for any reason.
  ## The maximum number of reruns allowed is 10. Default is 0, which implies no reruns.
  # num-flaky-test-attempts: 0

  # -- IosGcloudYml --

  ## The path to the test package (a zip file containing the iOS app and XCTest files).
  ## The given path may be in the local filesystem or in Google Cloud Storage using a URL beginning with gs://.
  ## Note: any .xctestrun file in this zip file will be ignored if --xctestrun-file is specified.
  test: ./src/test/kotlin/ftl/fixtures/tmp/earlgrey_example.zip

  ## The path to an .xctestrun file that will override any .xctestrun file contained in the --test package.
  ## Because the .xctestrun file contains environment variables along with test methods to run and/or ignore,
  ## this can be useful for customizing or sharding test suites. The given path should be in the local filesystem.
  ## Note: this path should usually be pointing to the xctestrun file within the derived data folder
  ## For example ./derivedDataPath/Build/Products/EarlGreyExampleSwiftTests_iphoneos13.4-arm64e.xctestrun
  xctestrun-file: ./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleSwiftTests_iphoneos13.4-arm64e.xctestrun

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
  # max-test-shards: 1

  ## shard time - the amount of time tests within a shard should take
  ## when set to > 0, the shard count is dynamically set based on time up to the maximum limit defined by max-test-shards
  ## 2 minutes (120) is recommended.
  ## default: -1 (unlimited)
  # shard-time: -1

  ## test runs - the amount of times to run the tests.
  ## 1 runs the tests once. 10 runs all the tests 10x
  # num-test-runs: 1

  ## Google cloud storage path to store the JUnit XML results from the last run.
  # smart-flank-gcs-path: gs://tmp_flank/flank/test_app_ios.xml

  ## Disables smart flank JUnit XML uploading. Useful for preventing timing data from being updated.
  ## Default: false
  # smart-flank-disable-upload: false

  ## Disables sharding. Useful for parameterized tests.
  # disable-sharding: false

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

  ## The billing enabled Google Cloud Platform project name to use
  # project: flank-open-source

  ## Local folder to store the test result. Folder is DELETED before each run to ensure only artifacts from the new run are saved.
  # local-result-dir: flank

  ## The max time this test run can execute before it is cancelled (default: unlimited).
  # run-timeout: 60m

  ## Keeps the full path of downloaded files. Required when file names are not unique.
  ## Default: false
  # keep-file-path: false

  ## The max time this test run can execute before it is cancelled (default: unlimited).
  # run-timeout: 60m

  ## Terminate with exit code 0 when there are failed tests.
  ## Useful for Fladle and other gradle plugins that don't expect the process to have a non-zero exit code.
  ## The JUnit XML is used to determine failure. (default: false)
  # ignore-failed-tests: true
```

### Android example

Run `test_runner/flank.yml` with flank to verify Android execution is working.

- `cd test_runner/`
- `./gradlew clean build shadowJar`
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

  ## Enable video recording during the test. Disabled by default. Use --record-video to enable.
  # record-video: true

  ## The max time this test execution can run before it is cancelled (default: 15m).
  ## It does not include any time necessary to prepare and clean up the target device.
  ## The maximum possible testing time is 30m on physical devices and 60m on virtual devices.
  ## The TIMEOUT units can be h, m, or s. If no unit is given, seconds are assumed.
  # timeout: 30m

  ## Invoke a test asynchronously without waiting for test results.
  # async: false

  ## A key-value map of additional details to attach to the test matrix.
  ## Arbitrary key-value pairs may be attached to a test matrix to provide additional context about the tests being run.
  ## When consuming the test results, such as in Cloud Functions or a CI system,
  ## these details can add additional context such as a link to the corresponding pull request.
  # client-details
  #   key1: value1
  #   key2: value2

  ## The name of the network traffic profile, for example LTE, HSPA, etc,
  ## which consists of a set of parameters to emulate network conditions when running the test
  ## (default: no network shaping; see available profiles listed by the `flank test network-profiles list` command).
  ## This feature only works on physical devices.
  # network-profile: LTE

  ## The history name for your test results (an arbitrary string label; default: the application's label from the APK manifest).
  ## All tests which use the same history name will have their results grouped together in the Firebase console in a time-ordered test history list.
  # results-history-name: android-history

  ## Experimental!
  ## The number of times a TestExecution should be re-attempted if one or more\nof its test cases fail for any reason.
  ## The maximum number of reruns allowed is 10. Default is 0, which implies no reruns.
  # num-flaky-test-attempts: 0

  # -- AndroidGcloudYml --

  ## The path to the application binary file.
  ## The path may be in the local filesystem or in Google Cloud Storage using gs:// notation.
  ## Android App Bundles are specified as .aab, all other files are assumed to be APKs.
  app: ../test_app/apks/app-debug.apk

  ## The path to the binary file containing instrumentation tests.
  ## The given path may be in the local filesystem or in Google Cloud Storage using a URL beginning with gs://.
  test: ../test_app/apks/app-debug-androidTest.apk

  ## Automatically log into the test device using a preconfigured Google account before beginning the test.
  ## Disabled by default. Use --auto-google-login to enable.
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

  ## A list of device-path: file-path pairs that indicate the device paths to push files to the device before starting tests, and the paths of files to push.
  ## Device paths must be under absolute, whitelisted paths (${EXTERNAL_STORAGE}, or ${ANDROID_DATA}/local/tmp).
  ## Source file paths may be in the local filesystem or in Google Cloud Storage (gs://…).
  # other-files
  #   - /sdcard/dir1/file1.txt: local/file.txt
  #   - /sdcard/dir2/file2.jpg: gs://bucket/file.jpg

  ## Monitor and record performance metrics: CPU, memory, network usage, and FPS (game-loop only).
  ## Disabled by default. Use --performance-metrics to enable.
  # performance-metrics: true

  ## Specifies the number of shards into which you want to evenly distribute test cases.
  ## The shards are run in parallel on separate devices. For example,
  ## if your test execution contains 20 test cases and you specify four shards, each shard executes five test cases.
  ## The number of shards should be less than the total number of test cases.
  ## The number of shards specified must be >= 1 and <= 50.
  ## This option cannot be used along max-test-shards and is not compatible with smart sharding.
  ## If you want to take benefits of smart sharding use max-test-shards instead.
  ## default: null
  # num-uniform-shards: 50

  ## The fully-qualified Java class name of the instrumentation test runner
  ## (default: the last name extracted from the APK manifest).
  # test-runner-class: com.foo.TestRunner

  ## A list of one or more test target filters to apply (default: run all test targets).
  ## Each target filter must be fully qualified with the package name, class name, or test annotation desired.
  ## Supported test filters by am instrument -e … include:
  ## class, notClass, size, annotation, notAnnotation, package, notPackage, testFile, notTestFile
  ## See https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner for more information.
  # test-targets:
  #  - class com.example.app.ExampleUiTest#testPasses

  ## A map of robo_directives that you can use to customize the behavior of Robo test.
  ## The type specifies the action type of the directive, which may take on values click, text or ignore.
  ## If no type is provided, text will be used by default.
  ## Each key should be the Android resource name of a target UI element and each value should be the text input for that element.
  ## Values are only permitted for text type elements, so no value should be specified for click and ignore type elements.
  # robo-directives:
  #   "text:input_resource_name": message
  #   "click:button_resource_name": ""

  ## The path to a Robo Script JSON file.
  ## The path may be in the local filesystem or in Google Cloud Storage using gs:// notation.
  ## You can guide the Robo test to perform specific actions by recording a Robo Script in Android Studio and then specifying this argument.
  ## Learn more at https://firebase.google.com/docs/test-lab/robo-ux-test#scripting.
  # robo-script: path_to_robo_script

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
  # max-test-shards: 1

  ## shard time - the amount of time tests within a shard should take
  ## when set to > 0, the shard count is dynamically set based on time up to the maximum limit defined by max-test-shards
  ## 2 minutes (120) is recommended.
  ## default: -1 (unlimited)
  # shard-time: -1

  ## test runs - the amount of times to run the tests.
  ## 1 runs the tests once. 10 runs all the tests 10x
  # num-test-runs: 1

  ## Google cloud storage path to store the JUnit XML results from the last run.
  # smart-flank-gcs-path: gs://tmp_flank/flank/test_app_android.xml

  ## Disables smart flank JUnit XML uploading. Useful for preventing timing data from being updated.
  ## Default: false
  # smart-flank-disable-upload: false

  ## Disables sharding. Useful for parameterized tests.
  # disable-sharding: false

  ## always run - these tests are inserted at the beginning of every shard
  ## useful if you need to grant permissions or login before other tests run
  # test-targets-always-run:
  #   - class com.example.app.ExampleUiTest#testPasses

  ## regex is matched against bucket paths, for example: 2019-01-09_00:13:06.106000_YCKl/shard_0/NexusLowRes-28-en-portrait/bugreport.txt
  # files-to-download:
  #   - .*\.mp4$

  ## The billing enabled Google Cloud Platform project name to use
  # project: flank-open-source

  ## Local folder to store the test result. Folder is DELETED before each run to ensure only artifacts from the new run are saved.
  # local-result-dir: flank

  ## Keeps the full path of downloaded files. Required when file names are not unique.
  ## Default: false
  # keep-file-path: false

  ## Include additional app/test apk pairs in the run. Apks are unique by just filename and not by path!
  ## If app is omitted, then the top level app is used for that pair.
  # additional-app-test-apks:
  #  - app: ../test_app/apks/app-debug.apk
  #    test: ../test_app/apks/app1-debug-androidTest.apk
  #  - test: ../test_app/apks/app2-debug-androidTest.apk

  ## The max time this test run can execute before it is cancelled (default: unlimited).
  # run-timeout: 60m

  ## Terminate with exit code 0 when there are failed tests.
  ## Useful for Fladle and other gradle plugins that don't expect the process to have a non-zero exit code.
  ## The JUnit XML is used to determine failure. (default: false)
  # ignore-failed-tests: true

  ## Flank provides two ways for parsing junit xml results.
  ## New way uses google api instead of merging xml files, but can generate slightly different output format.
  ## This flag allows fallback for legacy xml junit results parsing
  ## Currently available for android, iOS still uses only legacy way.
  # legacy-junit-result: false
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
        def javaClasses = fileTree(dir: "${project.buildDir}/intermediates/javac/debug/classes", excludes: excludes)
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
Starting from Android Marshmallow we must grant runtime permissions to write to external storage. Following snippet in test class solves that issue.
If you want to get coverage files when using orchestrator, you must set this Rule for each test class.

```java
import androidx.test.rule.GrantPermissionRule;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

class MyEspressoTest {

  @Rule
  GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
          READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);

  // other configuration and tests
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

Stable. Get the latest [stable version number](https://github.com/Flank/flank/releases/latest) and replace the `XXX` with the version number.

```
wget --quiet https://github.com/Flank/flank/releases/download/vXXX/flank.jar -O ./flank.jar
java -jar ./flank.jar android run
```

Snapshot (published after every commit)

```
wget --quiet https://github.com/Flank/flank/releases/download/flank_snapshot/flank.jar -O ./flank.jar
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

### Circle CI

Circle CI has a [firebase testlab orb](https://circleci.com/orbs/registry/orb/freeletics/firebase-testlab) that supports Flank.

### Bitrise

Bitrise has an official [flank step](https://github.com/bitrise-steplib/bitrise-step-flank).

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

### Maven

You can consume Flank via maven. See the [maven repo](https://bintray.com/flank/maven/flank) for all supported versions.

```
repositories {
    maven(url = "https://dl.bintray.com/flank/maven")
}

dependencies {
    compile("flank:flank:flank_snapshot")
}
```

### FAQ

1) > Access Not Configured. Cloud Tool Results API has not been used in project 764086051850 before or it is disabled.

    This error means authentication hasn't been setup properly. See `Authenticate with a service account` in this readme.

2)  > How do I use Flank without typing long commands?

    Add Flank's [bash helper folder](https://github.com/Flank/flank/blob/master/test_runner/bash/) to your $PATH environment variable. This will allow you to call the shell scripts in that helper folder from anywhere.

    With the [flank](https://github.com/Flank/flank/blob/master/test_runner/bash/flank) shell script, you can use `flank` instead of `java -jar flank.jar`. Examples:

    - `flank android run`
    - `flank ios run`

    With the [update_flank.sh](https://github.com/Flank/flank/blob/master/test_runner/bash/update_flank.sh) shell script, you can rebuild `flank.jar`.

3)  > Symbol is declared in module 'java.xml' which does not export package 'com.sun.org.apache.xerces.internal.dom'

    Make sure you're using JDK 8 to compile Flank.

4)  > Test run failed to complete. Expected 786 tests, received 660

    Try setting `use-orchestrator: false`. Parameterized tests [are not compatible with orchestrator](https://stackoverflow.com/questions/48735268/unable-to-run-parameterized-tests-with-android-test-orchestrator). Flank uses [orchestrator by default on Android.](https://developer.android.com/training/testing/junit-runner)

# Resources

- [Instrumenting Firebase Test Lab](https://developer.squareup.com/blog/instrumenting-firebase-test-lab/)
