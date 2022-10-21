# Flank [![codecov](https://codecov.io/gh/Flank/flank/branch/master/graph/badge.svg)](https://codecov.io/gh/Flank/flank)

Flank is a [massively parallel Android and iOS test runner](https://docs.google.com/presentation/d/1goan9cXpimSJsS3L60WjljnFA_seUyaWb2e-bezm084/edit#slide=id.p1) for [Firebase Test Lab](https://firebase.google.com/docs/test-lab/).

Flank is YAML compatible with [the gcloud CLI](https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test). Flank provides extra features to accelerate velocity and increase quality.

## Download

https://github.com/Flank/flank/releases/latest/download/flank.jar

## Contributing

- Install JDK 15 (it works also correctly on the previous version, a newer version is not guaranteed to work properly):
  - [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html#JDK15)
  - [OpenJDK](https://openjdk.java.net/projects/jdk/15/)
  - [AdoptJDK](https://adoptopenjdk.net/?variant=openjdk15&jvmVariant=hotspot)
- Use [JetBrains Toolbox](https://www.jetbrains.com/toolbox/app/) to install `IntelliJ IDEA Community`
- Clone the repo `git clone --recursive https://github.com/Flank/flank.git`
  - `git submodule update --init --recursive` updates the submodules
- Open `build.gradle.kts` in the main Flank base directory with `IntelliJ IDEA Community`, this will open the entire Flank mono repo
- test runner contributions can be made in the `test_runner\` subdirectory

## Features

- Test sharding
- Cost reporting
- Stability testing
- HTML report
- JUnit XML report
- Smart Flank

## Exit Codes

| Exit code  | Description |
|  ---:      | :---  |
| 0          | All tests passed
| 1          | A general failure occurred. Possible causes include: a filename that does not exist or an HTTP/network error.
| 2          | Usually indicates missing or wrong usage of flags, incorrect parameters, errors in config files.
| 10         | At least one matrix not finished (usually a FTL internal error) or unexpected error occurred.
| 15         | Firebase Test Lab could not determine if the test matrix passed or failed, because of an unexpected error.
| 18         | The test environment for this test execution is not supported because of incompatible test dimensions. This error might occur if the selected Android API level is not supported by the selected device type.
| 19         | The test matrix was canceled by the user.
| 20         | A test infrastructure error occurred.

## CLI

Flank supports CLI flags for each YAML parameter. The CLI flags are useful to selectively override YAML file values. Pass the `--help` flag to see the full documentation. For example: `flank android run --help`

CLI flags work well with environment variables. You can override a value like this:

> flank android run --local-result-dir=$APP_NAME

### Flank configuration

app, test, and xctestrun-file support `~`, environment variables, and globs (*, **) when resolving paths

### iOS example

Run `test_runner/flank.ios.yml` with flank to verify iOS execution is working.

- `./gradlew clean test_runner:build test_runner:shadowJar`
- `java -jar ./test_runner/build/libs/flank-*.jar firebase test ios run`

```yaml
# gcloud args match the official gcloud cli
# https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
gcloud:
  # -- GcloudYml --

  ### Results Bucket
  ## The name of a Google Cloud Storage bucket where raw test results will be stored
  # results-bucket: tmp_flank

  ### Results Directory
  ## The name of a unique Google Cloud Storage object within the results bucket where raw test results will be stored
  ## (default: a timestamp with a random suffix).
  # results-dir: tmp

  ### Record Video flag
  ## Enable video recording during the test. Disabled by default. Use --record-video to enable.
  # record-video: true

  ### Timeout
  ## The max time this test execution can run before it is cancelled (default: 15m).
  ## It does not include any time necessary to prepare and clean up the target device.
  ## The maximum possible testing time is 45m on physical devices and 60m on virtual devices.
  ## The TIMEOUT units can be h, m, or s. If no unit is given, seconds are assumed.
  # timeout: 30m

  ### Asynchronous flag
  ## Invoke a test asynchronously without waiting for test results.
  # async: false

  ### Client Details
  ## A key-value map of additional details to attach to the test matrix.
  ## Arbitrary key-value pairs may be attached to a test matrix to provide additional context about the tests being run.
  ## When consuming the test results, such as in Cloud Functions or a CI system,
  ## these details can add additional context such as a link to the corresponding pull request.
  # client-details
  #   key1: value1
  #   key2: value2

  ### Network Profile
  ## The name of the network traffic profile, for example LTE, HSPA, etc,
  ## which consists of a set of parameters to emulate network conditions when running the test
  ## (default: no network shaping; see available profiles listed by the `flank test network-profiles list` command).
  ## This feature only works on physical devices.
  # network-profile: LTE

  ### Result History Name
  ## The history name for your test results (an arbitrary string label; default: the application's label from the APK manifest).
  ## All tests which use the same history name will have their results grouped together in the Firebase console in a time-ordered test history list.
  # results-history-name: android-history

  ### Number of Flaky Test Attempts
  ## The number of times a TestExecution should be re-attempted if one or more\nof its test cases fail for any reason.
  ## The maximum number of reruns allowed is 10. Default is 0, which implies no reruns.
  # num-flaky-test-attempts: 0

  ### Fail Fast
  ## If true, only a single attempt at most will be made to run each execution/shard in the matrix.
  ## Flaky test attempts are not affected. Normally, 2 or more attempts are made if a potential
  ## infrastructure issue is detected. This feature is for latency sensitive workloads. The
  ## incidence of execution failures may be significantly greater for fail-fast matrices and support
  ## is more limited because of that expectation.
  # fail-fast: false

  # -- IosGcloudYml --

  ### IOS Test Package Path
  ## The path to the test package (a zip file containing the iOS app and XCTest files).
  ## The given path may be in the local filesystem or in Google Cloud Storage using a URL beginning with gs://.
  ## Note: any .xctestrun file in this zip file will be ignored if --xctestrun-file is specified.
  test: ./src/test/kotlin/ftl/fixtures/tmp/earlgrey_example.zip

  ### IOS XCTestrun File Path
  ## The path to an .xctestrun file that will override any .xctestrun file contained in the --test package.
  ## Because the .xctestrun file contains environment variables along with test methods to run and/or ignore,
  ## this can be useful for customizing or sharding test suites. The given path should be in the local filesystem.
  ## Note: this path should usually be pointing to the xctestrun file within the derived data folder
  ## For example ./derivedDataPath/Build/Products/EarlGreyExampleSwiftTests_iphoneos13.4-arm64e.xctestrun
  xctestrun-file: ./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleSwiftTests_iphoneos13.4-arm64e.xctestrun

  ### Xcode Version
  ## The version of Xcode that should be used to run an XCTest.
  ## Defaults to the latest Xcode version supported in Firebase Test Lab.
  ## This Xcode version must be supported by all iOS versions selected in the test matrix.
  # xcode-version: 10.1

  ### IOS Device Parameters
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

  ### Directories to Pull
  ## A list of paths that will be copied from the device's storage to the designated results bucket after the test
  ## is complete. These must be absolute paths under /private/var/mobile/Media or /Documents
  ## of the app under test. If the path is under an app's /Documents, it must be prefixed with the app's bundle id and a colon
  # directories-to-pull:
  #   - /private/var/mobile/Media

  ### Other File paths
  ## A list of device-path=file-path pairs that specify the paths of the test device and the files you want pushed to the device prior to testing.
  ## Device paths should either be under the Media shared folder (e.g. prefixed with /private/var/mobile/Media) or
  ## within the documents directory of the filesystem of an app under test (e.g. /Documents). Device paths to app
  ## filesystems should be prefixed by the bundle ID and a colon. Source file paths may be in the local filesystem or in Google Cloud Storage (gs://…).
  # other-files
  #   com.my.app:/Documents/file.txt: local/file.txt
  #   /private/var/mobile/Media/file.jpg: gs://bucket/file.jpg

  ### Additional IPA's
  ## List of up to 100 additional IPAs to install, in addition to the one being directly tested.
  ## The path may be in the local filesystem or in Google Cloud Storage using gs:// notation.
  # additional-ipas:
  #   - gs://bucket/additional.ipa
  #   - path/to/local/ipa/file.ipa

  ### Scenario Numbers
  ## A list of game-loop scenario numbers which will be run as part of the test (default: all scenarios).
  ## A maximum of 1024 scenarios may be specified in one test matrix, but the maximum number may also be limited by the overall test --timeout setting.
  # scenario-numbers:
  #   - 1
  #   - 2
  #   - 3

  ### Test type
  ## The type of iOS test to run. TYPE must be one of: xctest, game-loop. Default: xctest
  # type: xctest

  ### Application Path
  ## The path to the application archive (.ipa file) for game-loop testing.
  ## The path may be in the local filesystem or in Google Cloud Storage using gs:// notation.
  ## This flag is only valid when --type=game-loop is also set
  # app:
  #  - gs://bucket/additional.ipa OR path/to/local/ipa/file.ipa

  ### Testing with Special Entitlements
  ## Enables testing special app entitlements. Re-signs an app having special entitlements with a new application-identifier.
  ## This currently supports testing Push Notifications (aps-environment) entitlement for up to one app in a project.
  ## Note: Because this changes the app's identifier, make sure none of the resources in your zip file contain direct references to the test app's bundle id.
  # test-special-entitlements: false

flank:
  # -- FlankYml --

  ### Max Test Shards
  ## test shards - the amount of groups to split the test suite into
  ## set to -1 to use one shard per test. default: 1
  # max-test-shards: 1

  ## Shard Time
  ## shard time - the amount of time tests within a shard should take
  ## when set to > 0, the shard count is dynamically set based on time up to the maximum limit defined by max-test-shards
  ## 2 minutes (120) is recommended.
  ## default: -1 (unlimited)
  # shard-time: -1

  ### Number of Test Runs
  ## test runs - the amount of times to run the tests.
  ## 1 runs the tests once. 10 runs all the tests 10x
  # num-test-runs: 1

  ### Smart Flank GCS Paths
  ## Google cloud storage path to store the JUnit XML results from the last run.
  ## NOTE: Empty results will not be uploaded
  # smart-flank-gcs-path: gs://tmp_flank/flank/test_app_ios.xml

  ### Smart Flank Disable Upload flag
  ## Disables smart flank JUnit XML uploading. Useful for preventing timing data from being updated.
  ## Default: false
  # smart-flank-disable-upload: false

  ### Use Average Test Time For New Tests flag
  ## Enable using average time from previous tests duration when using SmartShard and tests did not run before.
  ## Default: false
  # use-average-test-time-for-new-tests: true

  ### Default Test Time
  ## Set default test time used for calculating shards.
  ## Default: 120.0
  # default-test-time: 15

  ### Default Class Test Time
  ## Set default test time (in seconds) used for calculating shards of parametrized classes when previous tests results are not available.
  ## Default test time for classes should be different from the default time for test
  ## Default: 240.0
  # default-class-test-time: 30

  ### Disable Sharding flag
  ## Disables sharding. Useful for parameterized tests.
  # disable-sharding: false

  ### Test targets always Run
  ## always run - these tests are inserted at the beginning of every shard
  ## Execution order is not guaranteed by Flank. Users are responsible for configuring their own device test runner logic.
  # test-targets-always-run:
  #   - className/testName

  ### Files to Download
  ## regex is matched against bucket paths, for example: 2019-01-09_00:18:07.314000_hCMY/shard_0/EarlGreyExampleSwiftTests_iphoneos12.1-arm64e.xctestrun
  # files-to-download:
  #   - .*\.mp4$

  # -- IosFlankYml --

  ### Test Targets
  ## test targets - a list of tests to run. omit to run all tests.
  # test-targets:
  #   - className/testName

  ### Billing Project ID
  ## The billing enabled Google Cloud Platform project id to use
  # project: flank-open-source

  ### Local Result Directory Storage
  ## Local folder to store the test result. Folder is DELETED before each run to ensure only artifacts from the new run are saved.
  # local-result-dir: flank

  ### Run Timeout
  ## The max time this test run can execute before it is cancelled (default: unlimited).
  # run-timeout: 60m

  ### Keep File Path flag
  ## Keeps the full path of downloaded files. Required when file names are not unique.
  ## Default: false
  # keep-file-path: false

  ### Ignore Failed Tests flag
  ## Terminate with exit code 0 when there are failed tests.
  ## Useful for Fladle and other gradle plugins that don't expect the process to have a non-zero exit code.
  ## The JUnit XML is used to determine failure. (default: false)
  # ignore-failed-tests: true

  ### Output Style flag
  ## Output style of execution status. May be one of [verbose, multi, single, compact].
  ## For runs with only one test execution the default value is 'verbose', in other cases
  ## 'multi' is used as the default. The output style 'multi' is not displayed correctly on consoles
  ## which don't support ansi codes, to avoid corrupted output use single or verbose.
  ## The output style `compact` is used to produce less detailed output, it prints just Args, test and matrix count, weblinks, cost, and result reports.
  # output-style: single

  ### Full Junit Result flag
  ## Enable create additional local junit result on local storage with failure nodes on passed flaky tests.
  # full-junit-result: false

  ### Disable Result Upload flag
  ## Disables flank results upload on gcloud storage.
  ## Default: false
  # disable-results-upload: false

  ### Disable usage statistics flag
  ## Disable sending usage statistics (without sensitive data) to the analytic tool.
  ## Default: false
  # disable-usage-statistics: false

  ### Only Test Configuration
  ## Constrains a test action to only test a specified test configuration within a test plan and exclude all other test configurations.
  ## Flank can combine multiple constraint options, but -only-test-configuration has precedence over -skip-test-configuration.
  ## Each test configuration name must match the name of a configuration specified in a test plan and is case-sensitive.
  ## Default: null (run all test configurations)
  # only-test-configuration: en

  ### Skip Test Configuration
  ## Constrains a test action to skip a specified test configuration and include all other test configurations.
  ## Flank can combine multiple constraint options, but -only-test-configuration has precedence over -skip-test-configuration.
  ## Each test configuration name must match the name of a configuration specified in a test plan and is case-sensitive.
  ## Default: null (run all test configurations)
  # skip-test-configuration: en

 ### Enable output report with set type
 ## Saves output results as parsable file and optionally upload it to Gcloud..
 ## Default: none
 # output-report: none

 ### Disable config validation (for both, yml and command line)
 ## If true, Flank won't validate options provided by the user. In general, it's not a good idea but,
 ## there are cases when this could be useful for a user
 ## (example: project can use devices that are not commonly available, the project has higher sharding limits, etc).
 ## Default: false
 # skip-config-validation: false

 ### Path to the custom sharding JSON file
 ## Flank will apply provided sharding to the configuration.
 ## For detailed explanation please check https://github.com/Flank/flank/blob/master/docs/feature/1665-custom-sharding.md
 # custom-sharding-json: ./custom_sharding.json
```

### Android example

Run `test_runner/flank.yml` with flank to verify Android execution is working.

- `./gradlew clean test_runner:build test_runner:shadowJar`
- `java -jar ./test_runner/build/libs/flank-*.jar firebase test android run`

```yaml
# gcloud args match the official gcloud cli
# See the docs for full gcloud details https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run
gcloud:
  # -- GcloudYml --

  ### Result Bucket
  ## The name of a Google Cloud Storage bucket where raw test results will be stored
  # results-bucket: tmp_flank

  ### Result Directory
  ## The name of a unique Google Cloud Storage object within the results bucket where raw test results will be stored
  ## (default: a timestamp with a random suffix).
  # results-dir: tmp

  ### Record Video flag
  ## Enable video recording during the test. Disabled by default. Use --record-video to enable.
  # record-video: true

  ### Timeout
  ## The max time this test execution can run before it is cancelled (default: 15m).
  ## It does not include any time necessary to prepare and clean up the target device.
  ## The maximum possible testing time is 45m on physical devices and 60m on virtual devices.
  ## The TIMEOUT units can be h, m, or s. If no unit is given, seconds are assumed.
  # timeout: 30m

  ### Asynchronous flag
  ## Invoke a test asynchronously without waiting for test results.
  # async: false

  ### Client Details
  ## A key-value map of additional details to attach to the test matrix.
  ## Arbitrary key-value pairs may be attached to a test matrix to provide additional context about the tests being run.
  ## When consuming the test results, such as in Cloud Functions or a CI system,
  ## these details can add additional context such as a link to the corresponding pull request.
  # client-details
  #   key1: value1
  #   key2: value2

  ### Network Profile
  ## The name of the network traffic profile, for example LTE, HSPA, etc,
  ## which consists of a set of parameters to emulate network conditions when running the test
  ## (default: no network shaping; see available profiles listed by the `flank test network-profiles list` command).
  ## This feature only works on physical devices.
  # network-profile: LTE

  ### Result History Name
  ## The history name for your test results (an arbitrary string label; default: the application's label from the APK manifest).
  ## All tests which use the same history name will have their results grouped together in the Firebase console in a time-ordered test history list.
  # results-history-name: android-history

  ### Number of Flaky Test Attempts
  ## The number of times a TestExecution should be re-attempted if one or more\nof its test cases fail for any reason.
  ## The maximum number of reruns allowed is 10. Default is 0, which implies no reruns.
  # num-flaky-test-attempts: 0

  ### Fail Fast
  ## If true, only a single attempt at most will be made to run each execution/shard in the matrix.
  ## Flaky test attempts are not affected. Normally, 2 or more attempts are made if a potential
  ## infrastructure issue is detected. This feature is for latency sensitive workloads. The
  ## incidence of execution failures may be significantly greater for fail-fast matrices and support
  ## is more limited because of that expectation.
  # fail-fast: false

  # -- AndroidGcloudYml --

  ## Android Application Path
  ## The path to the application binary file.
  ## The path may be in the local filesystem or in Google Cloud Storage using gs:// notation.
  ## Android App Bundles are specified as .aab, all other files are assumed to be APKs.
  app: ../test_projects/android/apks/app-debug.apk

  ### Android Binary File Path
  ## The path to the binary file containing instrumentation tests.
  ## The given path may be in the local filesystem or in Google Cloud Storage using a URL beginning with gs://.
  test: ../test_projects/android/apks/app-debug-androidTest.apk

  ### Additional APK's
  ## A list of up to 100 additional APKs to install, in addition to those being directly tested.
  ## The path may be in the local filesystem or in Google Cloud Storage using gs:// notation.
  # additional-apks: additional-apk1.apk,additional-apk2.apk,additional-apk3.apk

  ### Auto Google Login flag
  ## Automatically log into the test device using a preconfigured Google account before beginning the test.
  ## Disabled by default. Use --auto-google-login to enable.
  # auto-google-login: true

  ### Use Orchestrator Flag
  ## Whether each test runs in its own Instrumentation instance with the Android Test Orchestrator
  ## (default: Orchestrator is used). Disable with --no-use-orchestrator.
  ## See https://developer.android.com/training/testing/junit-runner.html#using-android-test-orchestrator
  # use-orchestrator: true

  ### Environment Variables
  ## A comma-separated, key=value map of environment variables and their desired values. This flag is repeatable.
  ## The environment variables are mirrored as extra options to the am instrument -e KEY1 VALUE1 … command and
  ## passed to your test runner (typically AndroidJUnitRunner)
  # environment-variables:
  #  coverage: true
  #  coverageFilePath: /sdcard/
  #  clearPackageData: true

  ### Directories to Pull
  ## A list of paths that will be copied from the device's storage to the designated results bucket after the test
  ## is complete. These must be absolute paths under /sdcard or /data/local/tmp
  # directories-to-pull:
  #   - /sdcard/

  ### Grant Permissions flag
  ## Whether to grant runtime permissions on the device before the test begins.
  ## By default, all permissions are granted. PERMISSIONS must be one of: all, none
  # grant-permissions: all

  ### Test Type
  ## The type of test to run. TYPE must be one of: instrumentation, robo, game-loop.
  # type: instrumentation

  ### Other Files
  ## A list of device-path: file-path pairs that indicate the device paths to push files to the device before starting tests, and the paths of files to push.
  ## Device paths must be under absolute, whitelisted paths (${EXTERNAL_STORAGE}, or ${ANDROID_DATA}/local/tmp).
  ## Source file paths may be in the local filesystem or in Google Cloud Storage (gs://…).
  # other-files
  #   - /sdcard/dir1/file1.txt: local/file.txt
  #   - /sdcard/dir2/file2.jpg: gs://bucket/file.jpg

  ### OBB Files
  ## A list of one or two Android OBB file names which will be copied to each test device before the tests will run (default: None).
  ## Each OBB file name must conform to the format as specified by Android (e.g. [main|patch].0300110.com.example.android.obb) and will be installed into <shared-storage>/Android/obb/<package-name>/ on the test device.
  # obb-files:
  #   - local/file/path/test1.obb
  #   - local/file/path/test2.obb

  ### Scenario Numbers
  ## A list of game-loop scenario numbers which will be run as part of the test (default: all scenarios).
  ## A maximum of 1024 scenarios may be specified in one test matrix, but the maximum number may also be limited by the overall test --timeout setting.
  # scenario-numbers:
  #   - 1
  #   - 2
  #   - 3

  ### Scenario Labels
  ## A list of game-loop scenario labels (default: None). Each game-loop scenario may be labeled in the APK manifest file with one or more arbitrary strings, creating logical groupings (e.g. GPU_COMPATIBILITY_TESTS).
  ## If --scenario-numbers and --scenario-labels are specified together, Firebase Test Lab will first execute each scenario from --scenario-numbers.
  ## It will then expand each given scenario label into a list of scenario numbers marked with that label, and execute those scenarios.
  # scenario-labels:
  #  - label1
  #  - label2

  ### OBB filenames
  ## A list of OBB required filenames. OBB file name must conform to the format as specified by Android e.g.
  ## [main|patch].0300110.com.example.android.obb which will be installed into <shared-storage>/Android/obb/<package-name>/ on the device.
  # obb-names:
  #   - [main|patch].<VERSION>.com.example.android.obb

  ### Performance Metric flag
  ## Monitor and record performance metrics: CPU, memory, network usage, and FPS (game-loop only).
  ## Disabled by default. Use --performance-metrics to enable.
  # performance-metrics: true

  ### Number of Uniform Shards
  ## Specifies the number of shards into which you want to evenly distribute test cases.
  ## The shards are run in parallel on separate devices. For example,
  ## if your test execution contains 20 test cases and you specify four shards, each shard executes five test cases.
  ## The number of shards should be less than the total number of test cases.
  ## The number of shards specified must be >= 1 and <= 50.
  ## This option cannot be used along max-test-shards and is not compatible with smart sharding.
  ## If you want to take benefits of smart sharding use max-test-shards instead.
  ## default: null
  # num-uniform-shards: 50

  ### Instrumentation Test Runner Class
  ## The fully-qualified Java class name of the instrumentation test runner
  ## (default: the last name extracted from the APK manifest).
  # test-runner-class: com.foo.TestRunner

  ### Test Targets
  ## A list of one or more test target filters to apply (default: run all test targets).
  ## Each target filter must be fully qualified with the package name, class name, or test annotation desired.
  ## Supported test filters by am instrument -e … include:
  ## class, notClass, size, annotation, notAnnotation, package, notPackage, testFile, notTestFile
  ## See https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner for more information.
  # test-targets:
  #  - class com.example.app.ExampleUiTest#testPasses

  ### Robo Directives
  ## A map of robo_directives that you can use to customize the behavior of Robo test.
  ## The type specifies the action type of the directive, which may take on values click, text or ignore.
  ## If no type is provided, text will be used by default.
  ## Each key should be the Android resource name of a target UI element and each value should be the text input for that element.
  ## Values are only permitted for text type elements, so no value should be specified for click and ignore type elements.
  # robo-directives:
  #   "text:input_resource_name": message
  #   "click:button_resource_name": ""

  ### Robo Scripts
  ## The path to a Robo Script JSON file.
  ## The path may be in the local filesystem or in Google Cloud Storage using gs:// notation.
  ## You can guide the Robo test to perform specific actions by recording a Robo Script in Android Studio and then specifying this argument.
  ## Learn more at https://firebase.google.com/docs/test-lab/robo-ux-test#scripting.
  # robo-script: path_to_robo_script

  ### Android Device Parameters
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

  ### test-targets-for-shard
  ## Specifies a group of packages, classes, and/or test cases to run in each shard (a group of test cases).
  ## The shards are run in parallel on separate devices. You can repeat this flag up to 50 times to specify multiple shards when one or more physical devices are selected,
  ## or up to 500 times when no physical devices are selected.
  ## Note: If you include the flags environment-variable or test-targets when running test-targets-for-shard, the flags are applied to all the shards you create.
  # test-target-for-shard:
  # - package com.package1.for.shard1
  # - class com.package2.for.shard2.Class

  ### parameterized-tests
  ## Specifies how to handle tests which contain the parameterization annotation.
  ## 4 options are available
  ## default: treat Parameterized tests as normal and shard accordingly
  ## ignore-all: Parameterized tests are ignored and not sharded
  ## shard-into-single: Parameterized tests are collected and put into a single shard
  ## shard-into-multiple: Parameterized tests are collected and sharded into different shards based upon matching names. (Experimental)
  ## Note: If left blank default is used. Default usage may result in significant increase/difference of shard times observed
  ## Note: If shard-into-single is used, a single additional shard is created that will run the Parameterized tests separately.
  ## Note: If shard-into-multiple is used, each parameterized test will be matched by its corresponding name and sharded into a separate shard.
  ##       This may dramatically increase the amount of expected shards depending upon how many parameterized tests are discovered.
  # parameterized-tests: default

flank:
  # -- FlankYml --

  ### Max Test Shards
  ## test shards - the amount of groups to split the test suite into
  ## set to -1 to use one shard per test. default: 1
  # max-test-shards: 1

  ### Shard Time
  ## shard time - the amount of time tests within a shard should take
  ## when set to > 0, the shard count is dynamically set based on time up to the maximum limit defined by max-test-shards
  ## 2 minutes (120) is recommended.
  ## default: -1 (unlimited)
  # shard-time: -1

  ### Number of Test Runs
  ## test runs - the amount of times to run the tests.
  ## 1 runs the tests once. 10 runs all the tests 10x
  # num-test-runs: 1

  ### Smart Flank GCS Path
  ## Google cloud storage path where the JUnit XML results from the last run is stored.
  ## NOTE: Empty results will not be uploaded
  # smart-flank-gcs-path: gs://tmp_flank/tmp/JUnitReport.xml

  ### Smart Flank Upload Disable flag
  ## Disables smart flank JUnit XML uploading. Useful for preventing timing data from being updated.
  ## Default: false
  # smart-flank-disable-upload: false

  ### Use Average Test Time for New Tests flag
  ## Enable using average time from previous tests duration when using SmartShard and tests did not run before.
  ## Default: false
  # use-average-test-time-for-new-tests: true

  ### Default Test Time
  ## Set default test time used for calculating shards.
  ## Default: 120.0
  # default-test-time: 15

  ### Default Class Test Time
  ## Set default test time (in seconds) used for calculating shards of parametrized classes when previous tests results are not available.
  ## Default test time for classes should be different from the default time for test
  ## Default: 240.0
  # default-class-test-time: 30

  ### Disable Sharding flag
  ## Disables sharding. Useful for parameterized tests.
  # disable-sharding: false

  ### Test Targets Always Run
  ## always run - these tests are inserted at the beginning of every shard
  ## Execution order is not guaranteed by Flank. Users are responsible for configuring their own device test runner logic.
  # test-targets-always-run:
  #   - class com.example.app.ExampleUiTest#testPasses

  ### Files to Download
  ## regex is matched against bucket paths, for example: 2019-01-09_00:13:06.106000_YCKl/shard_0/NexusLowRes-28-en-portrait/bugreport.txt
  # files-to-download:
  #   - .*\.mp4$

  ### Billing Project ID
  ## The billing enabled Google Cloud Platform id name to use
  # project: flank-open-source

  ### Local Results Directory
  ## Local folder to store the test result. Folder is DELETED before each run to ensure only artifacts from the new run are saved.
  # local-result-dir: flank

  ### Keep File Path flag
  ## Keeps the full path of downloaded files. Required when file names are not unique.
  ## Default: false
  # keep-file-path: false

  ### Additional App/Test APKS
  ## Include additional app/test apk pairs in the run. Apks are unique by just filename and not by path!
  ## If app is omitted, then the top level app is used for that pair.
  ## You can overwrite global config per each test pair.
  ## Currently supported options are: max-test-shards, test-targets, client-details, environment-variables, device
  # additional-app-test-apks:
  #  - app: ../test_projects/android/apks/app-debug.apk
  #    test: ../test_projects/android/apks/app1-debug-androidTest.apk
  #    device:
  #      - model: Nexus6P
  #        version: 27
  #  - test: ../test_projects/android/apks/app2-debug-androidTest.apk
  #    max-test-shards: 5

  ### Run Timeout
  ## The max time this test run can execute before it is cancelled (default: unlimited).
  # run-timeout: 60m

  ### Ignore Failed Test flag
  ## Terminate with exit code 0 when there are failed tests.
  ## Useful for Fladle and other gradle plugins that don't expect the process to have a non-zero exit code.
  ## The JUnit XML is used to determine failure. (default: false)
  # ignore-failed-tests: true

  ### Legacy Junit Results flag
  ## Flank provides two ways for parsing junit xml results.
  ## New way uses google api instead of merging xml files, but can generate slightly different output format.
  ## This flag allows fallback for legacy xml junit results parsing
  ## Currently available for android, iOS still uses only legacy way.
  # legacy-junit-result: false

  ### Output Style flag
  ## Output style of execution status. May be one of [verbose, multi, single, compact].
  ## For runs with only one test execution the default value is 'verbose', in other cases
  ## 'multi' is used as the default. The output style 'multi' is not displayed correctly on consoles
  ## which don't support ansi codes, to avoid corrupted output use single or verbose.
  ## The output style `compact` is used to produce less detailed output, it prints just Args, test and matrix count, weblinks, cost, and result reports.
  # output-style: single

  ### Full Junit Result flag
  ## Enable create additional local junit result on local storage with failure nodes on passed flaky tests.
  # full-junit-result: false

  ### Disable Results Upload flag
  ## Disables flank results upload on gcloud storage.
  ## Default: false
  # disable-results-upload: false

  ### Disable usage statistics flag
  ## Disable sending usage statistics (without sensitive data) to the analytic tool.
  ## Default: false
  # disable-usage-statistics: false

  ### Enable output report with set type
  ## Saves output results as parsable file and optionally upload it to Gcloud. Possible values are [none, json].
  ## Default: none
  # output-report: none

 ### Disable config validation (for both, yml and command line)
 ## If true, Flank won't validate options provided by the user. In general, it's not a good idea but,
 ## there are cases when this could be useful for a user
 ## (example: project can use devices that are not commonly available, the project has higher sharding limits, etc).
 ## Default: false
 # skip-config-validation: false

 ### Path to the custom sharding JSON file
 ## Flank will apply provided sharding to the configuration.
 ## For detailed explanation please check https://github.com/Flank/flank/blob/master/docs/feature/1665-custom-sharding.md
 # custom-sharding-json: ./custom_sharding.json
```

## Flank Wrapper

Flank wrapper is a solution to always run the latest version of Flank.
It will download the latest version of Flank itself always when it changed.
Using Flank wrapper is similar to using Flank, all options provided to Flank wrapper will be passed to Flank itself.
To download the latest version of Flank wrapper, please visit [GitHub releases](https://github.com/Flank/flank/releases)
and search for tag `flank_wrapper-XXX`. There are also shell and a batch wrapper over `.jar` file included.

## Android code coverage

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

## CI integration

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
  app: ../../test_projects/android/apks/app-debug.apk
  test: ../../test_projects/android/apks/app-debug-androidTest.apk
EOF
```

### Circle CI

Circle CI has a [firebase testlab orb](https://circleci.com/orbs/registry/orb/freeletics/firebase-testlab) that supports Flank.

### Bitrise

Bitrise has an official [flank step](https://github.com/bitrise-steplib/bitrise-step-flank).

## Gradle Plugin

[Fladle][fladle] is a Gradle plugin for Flank that provides DSL configuration and task based execution.

[fladle]: https://github.com/runningcode/fladle

## Flank on Windows
In order to build or run Flank using Windows please follow [guide](windows_wsl_guide.md) of building/running it using Windows WSL.
Native support is not currently supported.

## Authenticate with a Google account

Run `flank auth login`. Flank will save the credential to `~/.flank`. Google account authentication allows each person
to have a unique non-shared credential. A service account is still recommended for CI.

## Authenticate with a service account

Follow the [test lab docs](https://firebase.google.com/docs/test-lab/android/continuous) to create a service account.
- Save the credential to `$HOME/.config/gcloud/application_default_credentials.json` or set `GOOGLE_APPLICATION_CREDENTIALS` when using a custom path.
- Set the project id in flank.yml or set the `GOOGLE_CLOUD_PROJECT` environment variable.
- (Since 21.01) if `projectId` is not set in a config yml file, flank uses the first available project ID among the following sources:
    1. The project ID specified in the JSON credentials file pointed by the GOOGLE_APPLICATION_CREDENTIALS environment variable [fladle](https://runningcode.github.io/fladle/configuration/#serviceaccountcredentials)
    1. The project ID specified by the GOOGLE_CLOUD_PROJECT environment variable
    1. The project ID specified in the JSON credentials file `$HOME/.config/gcloud/application_default_credentials.json`

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

## Running with gcloud directly

flank.yml is compatible with the gcloud CLI.

- `gcloud firebase test android run flank.yml:gcloud`
- `gcloud alpha firebase test ios run flank.ios.yml:gcloud`

**NOTE:** You will need to [activate gcloud's service account](https://cloud.google.com/sdk/gcloud/reference/auth/activate-service-account) for the above commands to work.

## Doctor

Use the doctor command to check for errors in the YAML.

- `flank firebase test android doctor`
- `flank firebase test ios doctor`

## Check version

Flank supports printing the current version.

```bash
$ flank -v
v3.0-SNAPSHOT
```

## Maven

You can consume Flank via maven.
See the [maven repo](https://mvnrepository.com/artifact/com.github.flank/flank) for all supported versions.

```
repositories {
    mavenCentral()
}

dependencies {
    compile("flank:flank:master-SNAPSHOT")
}
```

or [GitHub packages](https://github.com/Flank/flank/packages/)

<details open>
<summary>Groovy</summary>

```groovy
dependencies {
    implementation "com.github.flank:flank:<latest version>"
}
```

</details>
<details>
<summary>Kotlin</summary>

```kotlin
dependencies {
    implementation("com.github.flank:flank:<latest version>")
}
```
</details>

## Gradle Enterprise Export API

It is possible to fetch metrics from Gradle builds. For detailed info please visit [Gradle Export API](https://docs.gradle.com/enterprise/export-api/)
and flank's example [gradle-export-api](https://github.com/Flank/flank/tree/master/samples/gradle-export-api).

## FAQ

1) > Access Not Configured. Cloud Tool Results API has not been used in project 764086051850 before or it is disabled.

    This error means authentication hasn't been setup properly. See `Authenticate with a service account` in this readme.

2)  > How do I use Flank without typing long commands?

    Add Flank's [bash helper folder](https://github.com/Flank/flank/blob/master/test_runner/bash/) to your $PATH environment variable. This will allow you to call the shell scripts in that helper folder from anywhere.

    With the [flank](https://github.com/Flank/flank/blob/master/test_runner/bash/flank) shell script, you can use `flank` instead of `java -jar flank.jar`. Examples:

    - `flank android run`
    - `flank ios run`

    With the [update_flank.sh](https://github.com/Flank/flank/blob/master/test_runner/bash/update_flank.sh) shell script, you can rebuild `flank.jar`.

3)  > Test run failed to complete. Expected 786 tests, received 660

    Try setting `use-orchestrator: false`. Parameterized tests [are not compatible with orchestrator](https://stackoverflow.com/questions/48735268/unable-to-run-parameterized-tests-with-android-test-orchestrator). Flank uses [orchestrator by default on Android.](https://developer.android.com/training/testing/junit-runner)

4) > I have an issue when attempting to sync the Flank Gradle project
   > Task 'prepareKotlinBuildScriptModel' not found in project ':test_runner'.
   > or similar

    - Make sure you do not change any module specific settings for Gradle
    - Clear IDE cache using `File > Invalidate Caches / Restart`
    - Re-import project using root `build.gradle.kts`
    - Sync project again

5) > Does Flank support Cucumber?

   Please check [document](cucumber_support.md) for more info

6) > How can I find project id?

  Please check the [firebase documentation](https://firebase.google.com/docs/projects/learn-more?hl=en#find_the_project_id) about finding the project id

7) > How do I run Flank with a proxy?

  `java -Dhttp.proxyHost=localhost -Dhttp.proxyPort=8080 -Dhttp.proxyUser=user -Dhttp.proxyPassword=pass -jar ./test_runner/build/libs/flank.jar firebase test android run`

  See [google-auth-library-java](https://github.com/googleapis/google-auth-library-java#configuring-a-proxy) for details.


# Resources

- [Instrumenting Firebase Test Lab](https://developer.squareup.com/blog/instrumenting-firebase-test-lab/)
