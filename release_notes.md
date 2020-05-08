## next (unreleased)
- [#764](https://github.com/Flank/flank/pull/771) Fix crash on parse some control chars. ([adamfilipow92](https://github.com/adamfilipow92))
- [#772](https://github.com/Flank/flank/pull/772) Fail fast when results-dir is incorrect. ([jan-gogo](https://github.com/jan-gogo))
- [#757](https://github.com/Flank/flank/pull/767) Reduce memory usage by using Reader and Writer instead of ByteArrays. ([jan-gogo](https://github.com/jan-gogo))
- [#763](https://github.com/Flank/flank/pull/763) Use "localhost" as default for hostname to fix backward compatibility. ([jan-gogo](https://github.com/jan-gogo))
- [#757](https://github.com/Flank/flank/pull/757) Print version and revision before each command. ([jan-gogo](https://github.com/jan-gogo))
- [#759](https://github.com/Flank/flank/pull/759) Add shard name for uploaded xctestrun files. ([pawelpasterz](https://github.com/pawelpasterz))
- [#755](https://github.com/Flank/flank/pull/755) Remove ascii doc generated section header. ([jan-gogo](https://github.com/jan-gogo))
- [#731](https://github.com/Flank/flank/pull/731) Refactor jUnit HTML report. ([Writhe](https://github.com/Writhe))
- [#754](https://github.com/Flank/flank/pull/754) Sync README.md flank.yml flank.ios.yml. ([jan-gogo](https://github.com/jan-gogo))
- [#746](https://github.com/Flank/flank/pull/746) Ignore apk with filtered out tests instead of failing. ([pawelpasterz](https://github.com/pawelpasterz))
- [#741](https://github.com/Flank/flank/pull/741) Allow APKs with zero tests. ([fondesa](https://github.com/fondesa))
- [#737](https://github.com/Flank/flank/pull/737) Generate ascii doc. ([jan-gogo](https://github.com/jan-gogo))
- [#720](https://github.com/Flank/flank/pull/720) Update group id from `flank` to `com.github.flank` ([bootstraponline](https://github.com/bootstraponline))
- [#714](https://github.com/Flank/flank/pull/714) Add support for num-uniform-shards option. ([jan-gogo](https://github.com/jan-gogo))
- [#712](https://github.com/Flank/flank/pull/712) Add keep file path for ios. ([pawelpasterz](https://github.com/pawelpasterz))
- [#711](https://github.com/Flank/flank/pull/711) Remove hardcoded height. ([pawelpasterz](https://github.com/pawelpasterz))
- [#708](https://github.com/Flank/flank/pull/708) Add ignore failed tests option to Flank. ([pawelpasterz](https://github.com/pawelpasterz))
- [#704](https://github.com/Flank/flank/pull/709) Add robo for robo-directives & robo-script options. ([jan-gogo](https://github.com/jan-gogo))
- [#704](https://github.com/Flank/flank/pull/704) Fix shards calculation when there are ignored tests and shardTime is -1. ([jan-gogo](https://github.com/jan-gogo))
- [#692](https://github.com/Flank/flank/pull/698) Add support for other-files option. ([jan-gogo](https://github.com/jan-gogo))
- [#695](https://github.com/Flank/flank/pull/695) Add support for additional-apks option. ([jan-gogo](https://github.com/jan-gogo))
- [#683](https://github.com/Flank/flank/pull/683) Print web link. ([pawelpasterz](https://github.com/pawelpasterz))
- [#692](https://github.com/Flank/flank/pull/692) Add support for network-profiles list command & --network-profile option. ([jan-gogo](https://github.com/jan-gogo))
- [#689](https://github.com/Flank/flank/pull/689) Add support for client-details option. ([jan-gogo](https://github.com/jan-gogo))
- [#687](https://github.com/Flank/flank/pull/687) Debug message printed after every command. ([pawelpasterz](https://github.com/pawelpasterz))
- [#684](https://github.com/Flank/flank/pull/684) Add overhead time to junit test case report. ([jan-gogo](https://github.com/jan-gogo))
- [#666](https://github.com/Flank/flank/pull/666) Use API instead of XML for result parsing for android. ([jan-gogo](https://github.com/jan-gogo))
- [#678](https://github.com/Flank/flank/pull/678) Skip Bugsnag initialization if user disabled gcloud analytics. ([pawelpasterz](https://github.com/pawelpasterz))
- [#672](https://github.com/Flank/flank/pull/672) Flank timeout feature. ([pawelpasterz](https://github.com/pawelpasterz))
- [#657](https://github.com/Flank/flank/pull/657) Fix execution hangs. ([pawelpasterz](https://github.com/pawelpasterz))
- [#654](https://github.com/Flank/flank/pull/654) Fix test filters when using both notPackage and notClass. ([jan-gogo](https://github.com/jan-gogo))
- [#648](https://github.com/Flank/flank/pull/648) Include @Ignore JUnit tests in JUnit XML. ([pawelpasterz](https://github.com/pawelpasterz))
- [#646](https://github.com/Flank/flank/pull/646) Adopt kotlin-logging as a logging framework. ([jan-gogo](https://github.com/jan-gogo))
- [#644](https://github.com/Flank/flank/pull/644) Use high performance options by default. Video, login, and perf metrics are now disabled by default. ([pawelpasterz](https://github.com/pawelpasterz))
- [#643](https://github.com/Flank/flank/pull/643) Add --dry option to android run & ios run. ([jan-gogo](https://github.com/jan-gogo))
- [#642](https://github.com/Flank/flank/pull/642) Flank doctor should exit 1 on validation issues. ([pawelpasterz](https://github.com/pawelpasterz))
- [#641](https://github.com/Flank/flank/pull/641) Move all deps to Deps.kt ([doodla](https://github.com/doodla))
- [#640](https://github.com/Flank/flank/pull/640) Update gradle to 6.2.1 and JaCoCo to 0.8.5. ([doodla](https://github.com/doodla))
- [#639](https://github.com/Flank/flank/pull/639) Cache all uploads and downloads to GCS. ([Kurt-Bonatz](https://github.com/Kurt-Bonatz))
- [#635](https://github.com/Flank/flank/pull/635) Default to 2m time estimate for unknown tests when sharding. ([RainNapper](https://github.com/RainNapper))
- [#621](https://github.com/Flank/flank/pull/621) Adopt server side sharding. Max shard count is 50. ([bootstraponline](https://github.com/bootstraponline))

## v8.1.0

- [#612](https://github.com/Flank/flank/pull/612) Print HtmlErrorReport location. ([bootstraponline](https://github.com/bootstraponline))
- [#615](https://github.com/Flank/flank/pull/615) Add `--keep-file-path` for Android when downloading assets from Google Cloud Storage. ([tahirhajizada](https://github.com/tahirhajizada))

## v8.0.1

- [#608](https://github.com/Flank/flank/pull/608) Use MatrixRollupOutcome to set exit code value. ([bootstraponline](https://github.com/bootstraponline))

## v8.0.0

- [#595](https://github.com/Flank/flank/pull/595) Rename `flaky-test-attempts` to `num-flaky-test-attempts`. Rename `repeat-tests` to `num-test-runs`. ([bootstraponline](https://github.com/bootstraponline))
- [#605](https://github.com/Flank/flank/pull/605) Improve exit code logging. Use matrix outcome to set exit code. ([bootstraponline](https://github.com/bootstraponline))
- [#597](https://github.com/Flank/flank/pull/597) Support parsing testLabExecutionId. ([yogurtearl](https://github.com/yogurtearl))
- [#599](https://github.com/Flank/flank/pull/599) Disable FAIL_ON_UNKNOWN_PROPERTIES for forward compatibility. ([narenkmanoharan](https://github.com/narenkmanoharan))

## v7.0.2

- [#589](https://github.com/Flank/flank/pull/589) Fix java.lang.NumberFormatException: empty String. ([vfadc](https://github.com/vfadc))
- [#587](https://github.com/Flank/flank/pull/587) Optimize polling. ([bootstraponline](https://github.com/bootstraponline))

## v7.0.1

- [#586](https://github.com/Flank/flank/pull/586) Poll devices in parallel. Fixes performance regression in Flank v7. ([bootstraponline](https://github.com/bootstraponline))

## v7.0.0

- [#574](https://github.com/Flank/flank/pull/574) Improve test shard error reporting. Update device catalog to use projectId. ([bootstraponline](https://github.com/bootstraponline))
- [#582](https://github.com/Flank/flank/pull/582) Fix iOS exit code when using flaky-test-attempts. Don't print environment-variables to stdout for security. ([bootstraponline](https://github.com/bootstraponline))
- [#584](https://github.com/Flank/flank/pull/584) Poll all test executions instead of only the first per matrix. ([bootstraponline](https://github.com/bootstraponline))
- [#585](https://github.com/Flank/flank/pull/585) Fix bug in smart flank when sharding tests that run in 0 seconds. ([bootstraponline](https://github.com/bootstraponline))

## v6.2.3

- [#567](https://github.com/Flank/flank/pull/567) Fix `--app` & `--test` on Android CLI. ([bootstraponline](https://github.com/bootstraponline))
- [#571](https://github.com/Flank/flank/pull/571) Add `flank ios run --dump-shards` and `flank android run --dump-shards` for debugging ([bootstraponline](https://github.com/bootstraponline))
- [#572](https://github.com/Flank/flank/pull/572) Fix exit code reporting when using `flaky-test-attempts` ([bootstraponline](https://github.com/bootstraponline))

## v6.2.2

- [#566](https://github.com/Flank/flank/pull/566) Fix `--test` & `--xctestrun-file` on iOS CLI. ([bootstraponline](https://github.com/bootstraponline))

## v6.2.1

- [#563](https://github.com/Flank/flank/pull/563) Fix CLI support for iOS. ([bootstraponline](https://github.com/bootstraponline))

## v6.2.0

- [#560](https://github.com/Flank/flank/pull/560) Add `--test-runner-class` support for Android. ([jschear](https://github.com/jschear))

## v6.1.0

- [#542](https://github.com/Flank/flank/pull/542) Add `additional-app-test-apks` to include multiple app/test apk pairs in a single run. ([bootstraponline](https://github.com/bootstraponline))

## v6.0.1

- [#550](https://github.com/Flank/flank/pull/550) Update dex-test-parser to fix Inherited crash. ([bootstraponline](https://github.com/bootstraponline))

## v6.0.0
- [#541](https://github.com/Flank/flank/pull/541) Rename `--test-shards` CLI flag to `--max-test-shards`. Add `--smart-flank-gcs-path` CLI flag. ([bootstraponline](https://github.com/bootstraponline))
- [#544](https://github.com/Flank/flank/pull/544) Fix empty test targets crash. ([bootstraponline](https://github.com/bootstraponline))
- [#548](https://github.com/Flank/flank/pull/548) Fix flank cancel. Print os name when sharding iOS tests. ([bootstraponline](https://github.com/bootstraponline))
- [#549](https://github.com/Flank/flank/pull/549) Update to llvm 8.0.0 and Swift 5.0.1. ([bootstraponline](https://github.com/bootstraponline))

## v5.1.0
- [#537](https://github.com/Flank/flank/pull/537) Add `smart-flank-disable-upload` yml option to prevent new results from overriding previous results. ([elihart](https://github.com/elihart))

## v5.0.2

- [#538](https://github.com/Flank/flank/pull/538) Update `dextestparser`. Fixes APK parsing crash. ([bootstraponline](https://github.com/bootstraponline))
- [#536](https://github.com/Flank/flank/pull/536) Always calculate exit code from matrix status instead of JUnit XML. ([bootstraponline](https://github.com/bootstraponline))

## v5.0.1

- [#534](https://github.com/Flank/flank/pull/534) Fix updateMatrixFile not saving `matrix_ids.json`. ([bootstraponline](https://github.com/bootstraponline))

## v5.0.0

- [#506](https://github.com/Flank/flank/pull/506) Add bugsnag reporting to detect Flank crashes. ([bootstraponline](https://github.com/bootstraponline))
- [#507](https://github.com/Flank/flank/pull/507) Improve error message when credentials fail to load, folder doesn't exist, and on bucket creation failure. Properly pass through user credential when checking the storage bucket. ([bootstraponline](https://github.com/bootstraponline))
- [#514](https://github.com/Flank/flank/pull/514) Rename `testShards` to `maxTestShards` ([miguelslemos](https://github.com/miguelslemos))
- [#518](https://github.com/Flank/flank/pull/518) Add deprecation warnings when old key names are used. `flank android doctor --fix` will auto fix the YAML file. ([bootstraponline](https://github.com/bootstraponline))
- [#519](https://github.com/Flank/flank/pull/519)  Rename `maxTestShards` to `max-test-shards`, `shardTime` to `shard-time`, `repeatTests` to `repeat-tests`, `smartFlankGcsPath` to `smart-flank-gcs-path`, `disableSharding` to `disable-sharding`. Moved `project` from `gcloud` to `flank` ([bootstraponline](https://github.com/bootstraponline))
- [#523](https://github.com/Flank/flank/pull/523) Add `--local-result-dir` to make it easy to find the test result at a fixed path. ([bootstraponline](https://github.com/bootstraponline))
- [#524](https://github.com/Flank/flank/pull/524) Fix iOS test sharding when there's a space in the path ([bootstraponline](https://github.com/bootstraponline))
- [#522](https://github.com/Flank/flank/pull/522) Correctly report test results and exit code when using `flaky-test-attempts` ([bootstraponline](https://github.com/bootstraponline))
- [#530](https://github.com/Flank/flank/pull/530) Save files downloaded with `files-to-download` to device root folder. Fix web links in HTML report. ([bootstraponline](https://github.com/bootstraponline))
- [#533](https://github.com/Flank/flank/pull/533) Update matrix_ids.json on every matrix change. Useful for debugging runs that crashed. ([jschear](https://github.com/jschear))

## v4.4.0

- [#505](https://github.com/Flank/flank/pull/505) Fix `flank auth login` by using `google-auth-library-java`. ([bootstraponline](https://github.com/bootstraponline))
- [#501](https://github.com/Flank/flank/pull/501) Fix nullability check in SavedMatrix. ([bootstraponline](https://github.com/bootstraponline))
- [#493](https://github.com/Flank/flank/pull/493) Fix getDefaultBucket timeout. ([bootstraponline](https://github.com/bootstraponline))

## v4.3.1

- [#491](https://github.com/Flank/flank/pull/491) Fix `shardTime` when `testShards` is `-1` ([Macarse](https://github.com/Macarse))

## v4.3.0

- [#436](https://github.com/Flank/flank/pull/436) Print how accurate test times are when using smart flank ([Macarse](https://github.com/Macarse))
- [#462](https://github.com/Flank/flank/pull/462) Always find all iOS tests when sharding. ([bootstraponline](https://github.com/bootstraponline))
- [#471](https://github.com/Flank/flank/pull/471) Add dynamic bucket counts for Smart Flank via `shardTime` ([Macarse](https://github.com/Macarse))
- [#473](https://github.com/Flank/flank/pull/473) Add `disableSharding` flag ([Macarse](https://github.com/Macarse))
- [#476](https://github.com/Flank/flank/pull/476) Retry matrix creation when FTL API errors. ([bootstraponline](https://github.com/bootstraponline))
- [#477](https://github.com/Flank/flank/pull/477) Fix large number merging on iOS ([bootstraponline](https://github.com/bootstraponline))
- [#478](https://github.com/Flank/flank/pull/478) Add iOS regular expression filtering for `test-targets` ([Macarse](https://github.com/Macarse))
- [#479](https://github.com/Flank/flank/pull/479) Improve invalid iOS regex error message ([bootstraponline](https://github.com/bootstraponline))
- [#481](https://github.com/Flank/flank/pull/481) Add iOS support for locale and orientation ([Macarse](https://github.com/Macarse))
- [#482](https://github.com/Flank/flank/pull/482) Document Android code coverage with orchestrator on FTL ([bootstraponline](https://github.com/bootstraponline))
- [#485](https://github.com/Flank/flank/pull/485) Replace : in folder name to improve Windows support. ([Sunil-plsr](https://github.com/Sunil-plsr))
- [#487](https://github.com/Flank/flank/pull/487) Document all Flank YAML properties in README ([bootstraponline](https://github.com/bootstraponline))

## v4.2.0

- Fix create Gcs bucket [#444](https://github.com/Flank/flank/pull/444)
- Add `files-to-download` to Android and iOS. Specify a list of regular expressions to download files from the Google Cloud Storage bucket. [#441](https://github.com/Flank/flank/pull/441)
- Add `flank auth login` to authorize with a user account instead of a service account. [#446](https://github.com/Flank/flank/pull/436)
- Add `flaky-test-attempts` support on Android and iOS which automatically retries failed tests. [#454](https://github.com/Flank/flank/pull/454)

## v4.1.1

- Add support for parameterized iOS tests when shard count is 1. [#435](https://github.com/Flank/flank/pull/435)
- Add support for `SkipTestIdentifiers` when parsing iOS `.xctestrun` files. [#435](https://github.com/Flank/flank/pull/435)

## v4.1.0

- `app`, `test`, and `xctestrun-file` now support `~`, environment variables, and globs (`*`, `**`) when resolving paths. [#386](https://github.com/Flank/flank/pull/386)
- Add CLI support for `flank android run` and `flank ios run`
- Add experimental `smartFlankGcsPath` to shard iOS and Android tests by time using historical run data. The amount of shards used is set by `testShards`. [#385](https://github.com/Flank/flank/pull/385)
- Fix parsing empty testcase [#402](https://github.com/Flank/flank/pull/402)
- Add progress bar when uploading files. [#403](https://github.com/Flank/flank/pull/403)
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
