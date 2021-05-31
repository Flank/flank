# Flank - Corellium

Run your tests in parallel on virtual devices driven by Corellium backend.

## Status

The Flank - Corellium integration is at the MVP stage, so only core, most important features are available.

[Read more](https://github.com/sparkrnyc/sparkr_general_docs/wiki/What-are-Prototype,-POC,-Alpha,-Beta-and-MVP#minimum-viable-product-mvp) about Minimum Viable Product.

## Why Corellium

Flank is just a client-side application that can prepare the time-efficient parallel test plan to run on several devices. It requires a third-party provider that can serve a huge amount of devices to run tests on them.   
Corellium is solving this problem as follows:

* Provides technology to virtualize mobile operating systems on servers powered by ARM cores. - Which is giving incredible ability for scaling amount of devices.
* Gives access to the bare operating system (Android or iOS). - Which is allowing to run optimized sharding algorithm for improving test execution time and reducing costs.

## How to get

### The Latest build

Flank - Corellium integration is built in the `flank.jar` executable, so the latest Flank build is giving you access to the features driven on the Corellium backend.

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

### Authorization file
## Path to YAML file with host address and credentials.
## default: corellium_auth.yml
# auth: auth_file.yml

### Project name
## The name of Corellium project.
## default: "Default project"
# project: "project name"

### Max Test Shards
## test shards - the amount of groups to split the test suite into.
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
```

### Command-line arguments

Instead of or along with configuration file you can specify same options, in a command-line, which will override config file options.

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

* JUnitReport.xml
* android_shards.json

# Features

* Calculating multi-module shards
* Creating or reusing instances (devices)
* Installing APKs on remote devices
* Running android tests
* Dumping shards to file
* Parsing `adb am instrument` logs
* Generating JUnit report

# Roadmap

* Cleaning devices after test execution.
* Reusing test cases duration for sharding.
* Flaky test detection.
* Structural logging.
* iOS support

# Known bugs

* Missing `<skipped/>` tag for skipped test cases in JUnit report 
