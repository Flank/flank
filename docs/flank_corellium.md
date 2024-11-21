# Flank - Corellium

Run mobile tests in parallel on virtual devices driven by Corellium backend.

## Status

The Flank - Corellium integration is at the MVP stage, so only the core and most important features are available.

Read more about [Minimum Viable Product](https://github.com/sparkrnyc/sparkr_general_docs/wiki/What-are-Prototype,-POC,-Alpha,-Beta-and-MVP#minimum-viable-product-mvp).

## Supported API levels

The following Android API levels are supported: 25, 27, 28, 29, and 30. Additional API levels may be added based on customer feedback.

## Why Corellium

Flank is just a client-side application that can prepare a time-efficient parallel test plan to run on several devices. It requires a third-party provider that can serve a huge amount of devices to run tests on them.   
Corellium is solving this problem as follows:

* Provides technology to virtualize mobile operating systems on servers powered by ARM cores. - This gives an incredible ability for scaling with regards to the amount of devices.
* Gives access to the bare operating system (Android or iOS). - Which allows a run optimized sharding algorithm that improves test execution time and reduces costs.

## How to get

### The Latest build

Flank - Corellium integration is built in the `flank.jar` executable, so the latest Flank build gives you access to the features driven on the Corellium backend.

### Manual compilation

Clone the repository and go to dir:

```shell
git clone git@github.com:Flank/flank.git
cd flank
```

Build flank using flank-scripts (this method will give you access to `flank.jar` through `flank` shell command):

```shell
. .env
flankScripts assemble flank
```

Or build directly using gradle command:

```shell
./gradlew :test_runner:build :test_runner:shadowJar
```

## How to run

To call the root command for Corellium related features:

locate your flank.jar in terminal and run:

```bash
flank.jar corellium
```

or if flank was build using `. .env && flankScripts assemble flank`, just type:

```bash
flank corellium
```

## Authorization

To allow Flank working with Corellium backend is necessary to provide authentication data. By default, Flank is recognizing `corellium_auth.yml` as an authentication file, which should look as follows:

```yaml
host: your.corellium.backend.host
username: your_username
password: your_password
```

## Android test execution

To execute android instrumented tests, run following command:

```bash
$ flank.jar corellium test android run -c="./flank_corellium.yml"
```

### Config

The example configuration file looks following:

```yaml
### Test apks
## The list of app and test apks.
## Each app apk must have one or more corresponding test apks.
apks:
  - path: "app1.apk"
    tests:
      - path: "app1-test1.apk"
  - path: "app2.apk"
    tests:
      - path: "app2-test1.apk"
      - path: "app2-test2.apk"

### Test Targets
## A list of one or more test target filters to apply (default: run all test targets).
## Each target filter must be fully qualified with the package name, class name, or test annotation desired.
## Supported test filters by am instrument -e … include:
## class, notClass, size, annotation, notAnnotation, package, notPackage, testFile, notTestFile
## See https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner for more information.
# test-targets:
#  - class com.example.app.ExampleUiTest#testPasses
#  - package com.example.app.foo
#  - notPackage com.example.app.bar

### Authorization file
## Path to YAML file with host address and credentials.
## default: corellium_auth.yml
# auth: auth_file.yml

### Project name
## The name of Corellium project.
## default: "Default project"
# project: "project name"

### Max Test Shards
## The amount of groups to split the test suite into.
## default: 1
# max-test-shards: 10000

### Results Directory
## The name of a local directory where test results will be stored.
## default: results/corellium/android/yyyy-MM-dd_HH-mm-ss-SSS
# local-result-dir: test-results

### Obfuscate dump
## Replace internal test names with unique identifiers, before dumping them to "android-shards.json".
## This option is hiding sensitive details about tests.
## default: false
# obfuscate: true

### Configure JUnit reports
## A map of name suffixes related to set of result types required to include in custom junit report.
## Available result types to include are: [Skipped, Passed, Failed, Flaky] (values are not case-sensitive).
## As results, this option will generate additional amount of junit reports named `JUnitReport-$suffix.xml`.
## For example the default configuration will generate JUnitReport-failures.xml.  
## default: failures: [Failed, Flaky]
# junit-report-config:
#   skipped: [Skipped]
#   passed: [PASSED]
#   failures: [failed, flaky]
```

### Command-line arguments

These can be included alongside the configuration file. They will override or supplement the configuration file depending on the usage.

### Execution

The test execution is composing small steps to fulfill operations like:

* Calculating time-efficient sharding.
* Preparing (creating or reusing) virtual devices for test execution.
* Executing tests on prepared virtual devices.
* Generating report.

To learn more about the execution process check the:

* [Architecture design](https://github.com/Flank/flank/blob/master/docs/architecture.md#domain-) - which is explaining some core abstract concepts.
* [Corellium integration diagrams](https://github.com/Flank/flank/tree/master/corellium#flank---corellium) - to visualise relations and dependencies between layers.
* [Domain module design & implementation](https://github.com/Flank/flank/tree/master/corellium/domain#flank---corellium---domain) - for detailed information about business logic and low-level implementation design.

### Results

The command execution process is generating different types of output.

#### Console logs

The process during its runtime is printing detailed information about execution steps to console log.

#### Output files

The successful run should generate the following files:

* JUnitReport.xml - raw report with reruns.
  * JUnitReport_failures.xml - only failed and flaky tests.
  * or any report files generated according to `junit-report-config` option.
* android_shards.json
* adb_log
  * Directory that contains dumped log from `am instrument` commands.
  * Each dump name is related instance id.

### Errors

The execution or its part can fail due to exceptions occur. 
Typically, most of the errors can be sourced in incorrect initial arguments or network issues.  

List of known possible errors:

#### Test result parsing

Flank is using `am instrument` command to execute tests on Corellium devices.
The console output of the device is collected and parsed until all expected tests return their results.
Due to an invalid apk file, the console can print unexpected output that cannot be parsed by Flank.
In this case, Flank will print an error message similar to the following:

```
Error while parsing results from instance { instance id }.
For details check "results/corellium/android/{ report_dir }/adb_log/{ instance id }" lines { from..to }.
java.lang.Exception
	at flank.corellium.cli.RunTestCorelliumAndroidCommandTest.outputTest(RunTestCorelliumAndroidCommandTest.kt:242)
	{ ... }
```

The visible exception is related directly to the parsing issue.
To see the source of the problem check the log file referenced in the error message. The file should contain a direct dump from `am instrument command`. 

# Features

* Filtering tests using test-targets.
* Calculating multi-module shards.
* Reusing test cases duration for sharding.
* Creating or reusing instances (devices).
* Installing APKs on remote devices.
* Running android tests.
* Flaky test detection.
* Dumping shards to file.
* Parsing `adb am instrument` logs.
* Generating JUnit report.

# Roadmap

* Cleaning devices after test execution.
* iOS support.
* and much more...
