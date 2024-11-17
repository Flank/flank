## Setup

- Install [Oracle JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  - JDK 9 or later will not work
- [IntelliJ Toolbox](https://www.jetbrains.com/toolbox/app/)
  - Use toolbox to install IntelliJ IDEA CE
  - PyCharm (also installed via toolbox) is useful for working with [gcloud CLI](https://github.com/bootstraponline/gcloud_cli)
- [GitHub Desktop](https://desktop.github.com/)

## Version Numbers

Flank follows date based releases.

- `YY.MM.#`
- `20.05.0` - First release in 2020/05
- `20.05.1` - Second release in 2020/05

# Debugging Flank

## Importing into IntelliJ IDEA

When opening the project in IntelliJ IDEA, select the "Import from Existing Sources" option and open the `test_runner` folder.

## Debugging Flank Commands

There is a [`Debug.kt`](https://github.com/Flank/flank/blob/master/test_runner/src/test/kotlin/Debug.kt#L8) which is your entry point. You can start a debug session here and set breakpoints wherever you need them. TIP: while debugging use `gs://...` links to avoid uploading apk/zip files

Note: The breakpoints will only hit for the actual command being run. For example, for `firebase test android run`, the debugger will directly enter [`AndroidRunCommand`](https://github.com/Flank/flank/blob/master/test_runner/src/main/kotlin/ftl/cli/firebase/test/android/AndroidRunCommand.kt).

The yml file being read is located at [test_runner/flank.yml](https://github.com/Flank/flank/blob/master/test_runner/flank.yml).

Run `./gradlew check` to fix lint issues

## Adding new gcloud property common to iOS and Android

- Add property to `GcloudYml` and update `keys` in config files
- Update `IArgs` with new property
- Update `AndroidArgs` to reference the property and update `toString`
- Update `IosArgs` to reference the property and `toString`
- Add test to `IosArgsTest`
- Add test to `IosRunCommandTest` and update `empty_params_parse_null` test
- Add test to `AndroidArgsTest`
- Add test to `AndroidRunCommandTest` and update `empty_params_parse_null` test
- Update Android/iOS example config in README.md, `flank.yml` and `flank.ios.yml`

## CLA

A CLA is required to contribute to flank. Google's open source policy [explains why CLAs are commonly used](https://opensource.google.com/docs/cla/policy/)
To sign CLA follow instructions on pull request which needs CLA to be signed. 
