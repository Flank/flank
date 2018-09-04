# Flank [![Build Status](https://app.bitrise.io/app/9767f3e19047d4db/status.svg?token=uDM3wCumR2xTd0axh4bjDQ&branch=master)](https://app.bitrise.io/app/9767f3e19047d4db) [![codecov](https://codecov.io/gh/TestArmada/flank/branch/master/graph/badge.svg)](https://codecov.io/gh/TestArmada/flank)

Flank is a [massively parallel Android and iOS test runner](https://medium.com/walmartlabs/flank-smart-test-runner-for-firebase-cf65e1b1eca7) for [Firebase Test Lab](https://firebase.google.com/docs/test-lab/).

### Contributing

- Use [JetBrains Toolbox](https://www.jetbrains.com/toolbox/app/) to install `IntelliJ IDEA Community`
- Clone the repo `git clone https://github.com/TestArmada/flank.git`
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
  # results-bucket: earlgrey-swift
  record-video: true
  timeout: 30m
  async: false
  # project: delta-essence-114723

  # test and xctestrun-file are the only required args
  test: ./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExample.zip
  xctestrun-file: ./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleSwiftTests_iphoneos11.2-arm64.xctestrun

flank:
  # test shards - the amount of groups to split the test suite into
  # set to -1 to use one shard per test.
  testShards: 1
  # test runs - the amount of times to run the tests.
  # 1 runs the tests once. 10 runs all the tests 10x
  testRuns: 1
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
  results-bucket: tmp_bucket_2
  record-video: true
  timeout: 60m
  async: false
  project: delta-essence-114723

  # test and app are the only required args
  app: ../../test_app/apks/app-debug.apk
  test: ../../test_app/apks/app-debug-androidTest.apk
  auto-google-login: true
  use-orchestrator: true
  environment-variables:
    clearPackageData: true
  directories-to-pull:
    - /sdcard/screenshots
  performance-metrics: true
  test-targets:
    - class com.example.app.ExampleUiTest#testPasses
  device:
    - model: NexusLowRes
      version: 28

flank:
  # test shards - the amount of groups to split the test suite into
  # set to -1 to use one shard per test.
  testShards: 1
  # test runs - the amount of times to run the tests.
  # 1 runs the tests once. 10 runs all the tests 10x
  testRuns: 1
  # always run - these tests are inserted at the beginning of every shard
  # useful if you need to grant permissions or login before other tests run
  test-targets-always-run:
    - class com.example.app.ExampleUiTest#testPasses
```

### CI integration

Download Flank from GitHub releases. The snapshot jar tracks master and is always up to date. Alternatively, rehost the jar to avoid automatically updating to new versions of Flank.

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

Place the [flank bash helper](https://github.com/TestArmada/flank/blob/master/test_runner/bash/flank) on the path to use `flank` instead of `java -jar flank.jar`. Examples:

- `flank android run`
- `flank ios run`
