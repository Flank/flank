## v4.4.0 (unreleased)
- 
- 

## v4.3.0

- Print how accurate test times are when using smart flank [#436](https://github.com/TestArmada/flank/pull/436) ([Macarse](https://github.com/Macarse))
- Always find all iOS tests when sharding. [#462](https://github.com/TestArmada/flank/pull/462) ([bootstraponline](https://github.com/bootstraponline))
- Add dynamic bucket counts for Smart Flank via `shardTime` [#471](https://github.com/TestArmada/flank/pull/471) ([Macarse](https://github.com/Macarse))
- Add `disableSharding` flag [#473](https://github.com/TestArmada/flank/pull/473) ([Macarse](https://github.com/Macarse))
- Retry matrix creation when FTL API errors.  [#476](https://github.com/TestArmada/flank/pull/476) ([bootstraponline](https://github.com/bootstraponline))
- Fix large number merging on iOS [#477](https://github.com/TestArmada/flank/pull/477) ([bootstraponline](https://github.com/bootstraponline))
- Add iOS regular expression filtering for `test-targets` [#478](https://github.com/TestArmada/flank/pull/478) ([Macarse](https://github.com/Macarse))
- Improve invalid iOS regex error message [#479](https://github.com/TestArmada/flank/pull/479) ([bootstraponline](https://github.com/bootstraponline))
- Add iOS support for locale and orientation [#481](https://github.com/TestArmada/flank/pull/481) ([Macarse](https://github.com/Macarse)
- Document Android code coverage with orchestrator on FTL [#482](https://github.com/TestArmada/flank/pull/482) ([bootstraponline](https://github.com/bootstraponline))
- Replace : in folder name to improve Windows support. [#485](https://github.com/TestArmada/flank/pull/482) ([Sunil-plsr](https://github.com/Sunil-plsr))
- Document all Flank YAML properties in README  [#487](https://github.com/TestArmada/flank/pull/487) ([bootstraponline](https://github.com/bootstraponline))


## v4.2.0

- Fix create Gcs bucket [#444](https://github.com/TestArmada/flank/pull/444)
- Add `files-to-download` to Android and iOS. Specify a list of regular expressions to download files from the Google Cloud Storage bucket. [#441](https://github.com/TestArmada/flank/pull/441)
- Add `flank auth login` to authorize with a user account instead of a service account. [#446](https://github.com/TestArmada/flank/pull/436)
- Add `flaky-test-attempts` support on Android and iOS which automatically retries failed tests. [#454](https://github.com/TestArmada/flank/pull/454)

## v4.1.1

- Add support for parameterized iOS tests when shard count is 1. [#435](https://github.com/TestArmada/flank/pull/435)
- Add support for `SkipTestIdentifiers` when parsing iOS `.xctestrun` files. [#435](https://github.com/TestArmada/flank/pull/435)

## v4.1.0

- `app`, `test`, and `xctestrun-file` now support `~`, environment variables, and globs (`*`, `**`) when resolving paths. [#386](https://github.com/TestArmada/flank/pull/386)
- Add CLI support for `flank android run` and `flank ios run`
- Add experimental `smartFlankGcsPath` to shard iOS and Android tests by time using historical run data. The amount of shards used is set by `testShards`. [#385](https://github.com/TestArmada/flank/pull/385)
- Fix parsing empty testcase [#402](https://github.com/TestArmada/flank/pull/402)
- Add progress bar when uploading files. [#403](https://github.com/TestArmada/flank/pull/403)
- iOS iPhone 8 default device has been updated from 11.2 to 12.0.

## v4.0.0

- Add `flank cancel` command. See `flank cancel --help`
- Add `flank refresh` command. See `flank refresh --help`
- Automatically detect projectId from service account credential
- Always generate aggregated JUnit XML reports
- Update HTML report to use create-react-app v2.1.0
- Add support for Xcode 10.1
- Improve test parsing to detect Swift tests that throw errors
- Update Flank exit codes, see readme for details.
- Link to matrix root when testing on multiple devices
