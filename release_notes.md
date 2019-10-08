## next (unreleased)

- [#595](https://github.com/TestArmada/flank/pull/595) Rename `flaky-test-attempts` to `num-flaky-test-attempts`. ([bootstraponline](https://github.com/bootstraponline))

## v7.0.2

- [#589](https://github.com/TestArmada/flank/pull/589) Fix java.lang.NumberFormatException: empty String. ([vfadc](https://github.com/vfadc))
- [#587](https://github.com/TestArmada/flank/pull/587) Optimize polling. ([bootstraponline](https://github.com/bootstraponline))

## v7.0.1

- [#586](https://github.com/TestArmada/flank/pull/586) Poll devices in parallel. Fixes performance regression in Flank v7. ([bootstraponline](https://github.com/bootstraponline))

## v7.0.0

- [#574](https://github.com/TestArmada/flank/pull/574) Improve test shard error reporting. Update device catalog to use projectId. ([bootstraponline](https://github.com/bootstraponline))
- [#582](https://github.com/TestArmada/flank/pull/582) Fix iOS exit code when using flaky-test-attempts. Don't print environment-variables to stdout for security. ([bootstraponline](https://github.com/bootstraponline))
- [#584](https://github.com/TestArmada/flank/pull/584) Poll all test executions instead of only the first per matrix. ([bootstraponline](https://github.com/bootstraponline))
- [#585](https://github.com/TestArmada/flank/pull/585) Fix bug in smart flank when sharding tests that run in 0 seconds. ([bootstraponline](https://github.com/bootstraponline))

## v6.2.3

- [#567](https://github.com/TestArmada/flank/pull/567) Fix `--app` & `--test` on Android CLI. ([bootstraponline](https://github.com/bootstraponline))
- [#571](https://github.com/TestArmada/flank/pull/571) Add `flank ios run --dump-shards` and `flank android run --dump-shards` for debugging ([bootstraponline](https://github.com/bootstraponline))
- [#572](https://github.com/TestArmada/flank/pull/572) Fix exit code reporting when using `flaky-test-attempts` ([bootstraponline](https://github.com/bootstraponline))

## v6.2.2

- [#566](https://github.com/TestArmada/flank/pull/566) Fix `--test` & `--xctestrun-file` on iOS CLI. ([bootstraponline](https://github.com/bootstraponline))

## v6.2.1

- [#563](https://github.com/TestArmada/flank/pull/563) Fix CLI support for iOS. ([bootstraponline](https://github.com/bootstraponline))

## v6.2.0

- [#560](https://github.com/TestArmada/flank/pull/560) Add `--test-runner-class` support for Android. ([jschear](https://github.com/jschear))

## v6.1.0

- [#542](https://github.com/TestArmada/flank/pull/542) Add `additional-app-test-apks` to include multiple app/test apk pairs in a single run. ([bootstraponline](https://github.com/bootstraponline))

## v6.0.1

- [#550](https://github.com/TestArmada/flank/pull/550) Update dex-test-parser to fix Inherited crash. ([bootstraponline](https://github.com/bootstraponline))

## v6.0.0
- [#541](https://github.com/TestArmada/flank/pull/541) Rename `--test-shards` CLI flag to `--max-test-shards`. Add `--smart-flank-gcs-path` CLI flag. ([bootstraponline](https://github.com/bootstraponline))
- [#544](https://github.com/TestArmada/flank/pull/544) Fix empty test targets crash. ([bootstraponline](https://github.com/bootstraponline))
- [#548](https://github.com/TestArmada/flank/pull/548) Fix flank cancel. Print os name when sharding iOS tests. ([bootstraponline](https://github.com/bootstraponline))
- [#549](https://github.com/TestArmada/flank/pull/549) Update to llvm 8.0.0 and Swift 5.0.1. ([bootstraponline](https://github.com/bootstraponline))

## v5.1.0
- [#537](https://github.com/TestArmada/flank/pull/537) Add `smart-flank-disable-upload` yml option to prevent new results from overriding previous results. ([elihart](https://github.com/elihart))

## v5.0.2

- [#538](https://github.com/TestArmada/flank/pull/538) Update `dextestparser`. Fixes APK parsing crash. ([bootstraponline](https://github.com/bootstraponline))
- [#536](https://github.com/TestArmada/flank/pull/536) Always calculate exit code from matrix status instead of JUnit XML. ([bootstraponline](https://github.com/bootstraponline))

## v5.0.1

- [#534](https://github.com/TestArmada/flank/pull/534) Fix updateMatrixFile not saving `matrix_ids.json`. ([bootstraponline](https://github.com/bootstraponline))

## v5.0.0

- [#506](https://github.com/TestArmada/flank/pull/506) Add bugsnag reporting to detect Flank crashes. ([bootstraponline](https://github.com/bootstraponline))
- [#507](https://github.com/TestArmada/flank/pull/507) Improve error message when credentials fail to load, folder doesn't exist, and on bucket creation failure. Properly pass through user credential when checking the storage bucket. ([bootstraponline](https://github.com/bootstraponline))
- [#514](https://github.com/TestArmada/flank/pull/514) Rename `testShards` to `maxTestShards` ([miguelslemos](https://github.com/miguelslemos))
- [#518](https://github.com/TestArmada/flank/pull/518) Add deprecation warnings when old key names are used. `flank android doctor --fix` will auto fix the YAML file. ([bootstraponline](https://github.com/bootstraponline))
- [#519](https://github.com/TestArmada/flank/pull/519)  Rename `maxTestShards` to `max-test-shards`, `shardTime` to `shard-time`, `repeatTests` to `repeat-tests`, `smartFlankGcsPath` to `smart-flank-gcs-path`, `disableSharding` to `disable-sharding`. Moved `project` from `gcloud` to `flank` ([bootstraponline](https://github.com/bootstraponline))
- [#523](https://github.com/TestArmada/flank/pull/523) Add `--local-result-dir` to make it easy to find the test result at a fixed path. ([bootstraponline](https://github.com/bootstraponline))
- [#524](https://github.com/TestArmada/flank/pull/524) Fix iOS test sharding when there's a space in the path ([bootstraponline](https://github.com/bootstraponline))
- [#522](https://github.com/TestArmada/flank/pull/522) Correctly report test results and exit code when using `flaky-test-attempts` ([bootstraponline](https://github.com/bootstraponline))
- [#530](https://github.com/TestArmada/flank/pull/530) Save files downloaded with `files-to-download` to device root folder. Fix web links in HTML report. ([bootstraponline](https://github.com/bootstraponline))
- [#533](https://github.com/TestArmada/flank/pull/533) Update matrix_ids.json on every matrix change. Useful for debugging runs that crashed. ([jschear](https://github.com/jschear))

## v4.4.0

- [#505](https://github.com/TestArmada/flank/pull/505) Fix `flank auth login` by using `google-auth-library-java`. ([bootstraponline](https://github.com/bootstraponline))
- [#501](https://github.com/TestArmada/flank/pull/501) Fix nullability check in SavedMatrix. ([bootstraponline](https://github.com/bootstraponline))
- [#493](https://github.com/TestArmada/flank/pull/493) Fix getDefaultBucket timeout. ([bootstraponline](https://github.com/bootstraponline))

## v4.3.1

- [#491](https://github.com/TestArmada/flank/pull/491) Fix `shardTime` when `testShards` is `-1` ([Macarse](https://github.com/Macarse))

## v4.3.0

- [#436](https://github.com/TestArmada/flank/pull/436) Print how accurate test times are when using smart flank ([Macarse](https://github.com/Macarse))
- [#462](https://github.com/TestArmada/flank/pull/462) Always find all iOS tests when sharding. ([bootstraponline](https://github.com/bootstraponline))
- [#471](https://github.com/TestArmada/flank/pull/471) Add dynamic bucket counts for Smart Flank via `shardTime` ([Macarse](https://github.com/Macarse))
- [#473](https://github.com/TestArmada/flank/pull/473) Add `disableSharding` flag ([Macarse](https://github.com/Macarse))
- [#476](https://github.com/TestArmada/flank/pull/476) Retry matrix creation when FTL API errors. ([bootstraponline](https://github.com/bootstraponline))
- [#477](https://github.com/TestArmada/flank/pull/477) Fix large number merging on iOS ([bootstraponline](https://github.com/bootstraponline))
- [#478](https://github.com/TestArmada/flank/pull/478) Add iOS regular expression filtering for `test-targets` ([Macarse](https://github.com/Macarse))
- [#479](https://github.com/TestArmada/flank/pull/479) Improve invalid iOS regex error message ([bootstraponline](https://github.com/bootstraponline))
- [#481](https://github.com/TestArmada/flank/pull/481) Add iOS support for locale and orientation ([Macarse](https://github.com/Macarse))
- [#482](https://github.com/TestArmada/flank/pull/482) Document Android code coverage with orchestrator on FTL ([bootstraponline](https://github.com/bootstraponline))
- [#485](https://github.com/TestArmada/flank/pull/485) Replace : in folder name to improve Windows support. ([Sunil-plsr](https://github.com/Sunil-plsr))
- [#487](https://github.com/TestArmada/flank/pull/487) Document all Flank YAML properties in README ([bootstraponline](https://github.com/bootstraponline))

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
