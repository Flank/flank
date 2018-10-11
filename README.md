# Flank [![Build Status](https://app.bitrise.io/app/9767f3e19047d4db/status.svg?token=uDM3wCumR2xTd0axh4bjDQ&branch=master)](https://app.bitrise.io/app/9767f3e19047d4db) [![codecov](https://codecov.io/gh/TestArmada/flank/branch/master/graph/badge.svg)](https://codecov.io/gh/TestArmada/flank)

Flank is a [massively parallel Android and iOS test runner](https://medium.com/walmartlabs/flank-smart-test-runner-for-firebase-cf65e1b1eca7) for [Firebase Test Lab](https://firebase.google.com/docs/test-lab/).

### Contributing

- Install [Oracle JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  - JDK 9 or later will not work
- Use [JetBrains Toolbox](https://www.jetbrains.com/toolbox/app/) to install `IntelliJ IDEA Community`
- Clone the repo `git clone --recursive https://github.com/TestArmada/flank.git`
  - `git submodule update --init --recursive` updates the submodules
- Open `test_runner/build.gradle.kts` with `IntelliJ IDEA Community`

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
  xctestrun-file: ./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleSwiftTests_iphoneos11.2-arm64.xctestrun
  xcode-version: 9.2
  device:
    - model: iphone8
      version: 11.2
      locale: en
      orientation: portrait

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
  timeout: 60m
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
```

### CI integration

Download Flank from GitHub releases.

Stable

```
wget --quiet https://github.com/TestArmada/flank/releases/download/v3.0.0/flank.jar -O ./flank.jar
```

Snapshot (published after every commit)

```
wget --quiet https://github.com/TestArmada/flank/releases/download/flank_snapshot/flank.jar -O ./flank.jar
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

### Authenticate with a service account

Follow the [test lab docs](https://firebase.google.com/docs/test-lab/android/continuous) to create a service account.
- Save the credential to `$HOME/.config/gcloud/application_default_credentials.json` or set `GOOGLE_APPLICATION_CREDENTIALS` when using a custom path.
- Set the project id in flank.yml or set the `GOOGLE_CLOUD_PROJECT` environment variable.

For continuous integration, base64 encode the credential as `GCLOUD_KEY`. Then write the file using a shell script. Note that gcloud CLI does not need to be installed. Flank works without any dependency on gcloud CLI.

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
