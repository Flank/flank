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
  # results-bucket: tmp_flank
  record-video: true
  timeout: 30m
  async: false
  # project: delta-essence-114723
  # results-history-name: ios-history

  # test and xctestrun-file are the only required args
  test: ./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExample.zip
  xctestrun-file: ./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleSwiftTests_iphoneos12.1-arm64e.xctestrun
  xcode-version: 9.2
  device:
    - model: iphone8
      version: 11.2
      locale: en
      orientation: portrait
  # The number of times to retry failed tests. Default is 0. Max is 10.
  flaky-test-attempts: 0

flank:
  # test shards - the amount of groups to split the test suite into
  # set to -1 to use one shard per test.
  testShards: 1
  # repeat tests - the amount of times to run the tests.
  # 1 runs the tests once. 10 runs all the tests 10x
  repeatTests: 1
  # always run - these tests are inserted at the beginning of every shard
  # useful if you need to grant permissions or login before other tests run
  test-targets-always-run:
    - a/testGrantPermissions
  # test targets - a list of tests to run. omit to run all tests.
  test-targets:
    - b/testBasicSelection
  # regex is matched against bucket paths, for example: 2019-01-09_00:18:07.314000_hCMY/shard_0/EarlGreyExampleSwiftTests_iphoneos12.1-arm64e.xctestrun
  files-to-download:
    - .*\.png$
```

### Android example

Run `test_runner/flank.yml` with flank to verify Android execution is working.

- `cd test_runner/`
- `./gradlew clean assemble fatJar`
- `java -jar ./build/libs/flank-*.jar firebase test android run`

```yaml
# gcloud args match the official gcloud cli
# https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run
gcloud:
  results-bucket: tmp_flank
  record-video: true
  timeout: 30m
  async: false
  project: delta-essence-114723
  results-history-name: android-history

  # test and app are the only required args
  app: ../test_app/apks/app-debug.apk
  test: ../test_app/apks/app-debug-androidTest.apk
  auto-google-login: true
  use-orchestrator: true
  environment-variables:
    clearPackageData: true
  directories-to-pull:
    - /sdcard/screenshots
  performance-metrics: true
  test-targets:
    # supported are class, notClass, size, annotation, notAnnotation, package, notPackage, testFile, notTestFile
    # as described in https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner
    - class com.example.app.ExampleUiTest#testPasses
  device:
    - model: NexusLowRes
      version: 28
  # The number of times to retry failed tests. Default is 0. Max is 10.
  flaky-test-attempts: 0

flank:
  # test shards - the amount of groups to split the test suite into
  # set to -1 to use one shard per test.
  testShards: 1
  # repeat tests - the amount of times to run the tests.
  # 1 runs the tests once. 10 runs all the tests 10x
  repeatTests: 1
  # always run - these tests are inserted at the beginning of every shard
  # useful if you need to grant permissions or login before other tests run
  test-targets-always-run:
    - class com.example.app.ExampleUiTest#testPasses
 # regex is matched against bucket paths, for example: 2019-01-09_00:13:06.106000_YCKl/shard_0/NexusLowRes-28-en-portrait/bugreport.txt
  files-to-download:
    - .*\.mp4$
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
