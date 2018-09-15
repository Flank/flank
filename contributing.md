## Setup
- Install [Oracle JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  - JDK 9 or later will not work
- [IntelliJ Toolbox](https://www.jetbrains.com/toolbox/app/)
  - Use toolbox to install IntelliJ IDEA CE
  - PyCharm (also installed via toolbox) is useful for working with [gcloud CLI](https://github.com/bootstraponline/gcloud_cli)
- [GitHub Desktop](https://desktop.github.com/)

## Working on the code

- Open `test_runner/build.gradle.kts` with `IntelliJ IDEA Community`
- There's an [issue tracker](https://github.com/TestArmada/flank/issues) and [project board]( https://github.com/TestArmada/flank/projects/1)
- Run `./gradlew ktlintFormat` to fix lint issues
- Run `./gralew ktlintApplyToIdea` to update Idea to use the [ktlint](https://github.com/shyiko/ktlint) settings

See the main readme for instructions on how to run the iOS and Android samples.

## Adding new gcloud property common to iOS and Android

- Add property to `GcloudYml` and update `keys`
- Update `IArgs` with new property
- Update `AndroidArgs` to reference the propery and update `toString`
- Update `IosArgs` to reference the propery and `toString`

## CLA

A CLA is required to contribute to flank. See [walmart labs](https://github.com/walmartlabs/walmart-cla#walmart-contributor-license-agreement-cla) for more information. Google's open source policy [explains why CLAs are commonly used](https://opensource.google.com/docs/cla/policy/)
