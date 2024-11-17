# Test Sharding

# Orchestrator

[Android Test Orchestrator][orchestrator] removes shared state and isolates crashes. Orchestrator trades performance for stability. Tests run slower when orchestrator is enabled.

Orchestrator ensures each tests runs Each test runs in a new [Instrumentation][instrumentation] instance to ensure there's no shared state. It's recommended to use `clearPackageData` as well to remove file system state. 

When a test crashes, only that tests instrumentation instance crashes which enables other tests in the suite to continue execution. Without orchestrator, a test crash will break the entire test suite.

```yaml
gcloud:
  use-orchestrator: true
```

## Orchestrator + Firebase Test lab

Orchestrator is **enabled by default in Flank** and disabled by default in gcloud CLI.

[AndroidX Test 1.3.0 Beta02][androidx_test] or later is required to run parameterized tests with orchestrator. Both the runner and orchestrator must be updated.

Local execution will use the version of Orchestrator defined in your gradle file. Firebase Test Lab currently uses Orchestrator v1.3.0. Firebase must manually upgrade Orchestrator on their devices for customers to use the new version.

[orchestrator]: https://developer.android.com/training/testing/junit-runner#using-android-test-orchestrator
[instrumentation]: https://developer.android.com/reference/android/app/Instrumentation
[androidx_test]: https://github.com/android/android-test/releases/tag/androidx-test-1.3.0-beta02


# GCloud Sharding

The [gcloud CLI][gcloud_cli] supports two types of sharding, uniform sharding and manual sharding via test targets for shard.

[--num-uniform-shards][test_targets_for_shards] is translated to “-e numShard” “-e shardIndex” [AndroidJUnitRunner][ajur] arguments. This uses the native `adb am instrument` sharding feature which randomly shards all tests. When using uniform shards, it's possible to have shards with empty tests.  

Firebase will mark shards as failed when execution consists of skipped/empty tests, as this is likely an indication the tests are not configured correctly. This firebase design choice is incompatible with num-uniform-shards as you will randomly get failures when shards are empty. Using num-uniform-shards is therefor not recommended.

[--test-targets-for-shard][test_targets_for_shards] allows manually specifying tests to be run.

On FTL `--test-targets` or `--test-targets-for-shard` are passed as arguments to `adb shell am instrument`.

> adb shell am instrument -r -w -e class com.example.test_app.ParameterizedTest#shouldHopefullyPass com.example.test_app.test/androidx.test.runner.AndroidJUnitRunner

[gcloud_cli]: https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/android/run
[uniform_shards]: https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/android/run#--num-uniform-shards
[test_targets_for_shards]: https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/android/run#--test-targets-for-shard
[ajur]: https://developer.android.com/training/testing/junit-runner#sharding-tests


# Flank
The Flank supports two types of sharding, automatic sharding and uniform sharding.

Automatic sharding is enabled by default and can be disabled by `--disable-sharding=true`. Under the hood automatic sharding uses same API as gcloud's `--test-targets-for-shard` but in difference to gcloud it creates shards automatically. 
`--dump-shards` can help with verifying if calculates shards are correct. Automated sharding can work with `--test-targets` option. 

`--num-uniform-shards` provides same functionality as gcloud's option. Is not compatible with automatic sharding.


# Parameterized tests


Flank [v20.06.1](https://github.com/Flank/flank/releases/tag/v20.06.1) fix some compatibility issues with named parameterized tests, when running with sharding. \
[Table](https://github.com/Flank/flank_parametrized_tests/tree/master/test_apk) below bases on [report](https://github.com/Flank/flank_parametrized_tests/tree/master/test_apk/report) from running parameterized tests with different configurations.
Flank uses same API as gcloud so everything supported by gcloud should be also supported by flank. 

|                   | orchestrator                              | disabled  | disabled  | 1.3.0-rc01 | 1.3.0-rc01 |
| ---               | ---:                                      | ---       | ---       | ---        | ---        |					
|                   | sharding                                  | disabled  | enabled   | disabled   | enabled    |
| local             | @RunWith(Parameterized::class)            | OK        | OK        | OK         | OK         |
| local             | @RunWith(Parameterized::class) {named}    | OK        | OK        | OK         | OK         |
| local             | @RunWith(JUnitParamsRunner::class)        | OK        | OK        | OK         | OK         |
| gcloud            | @RunWith(Parameterized::class)            | OK        | OK        | OK         | OK	      |		
| gcloud            | @RunWith(Parameterized::class) {named}    | OK        | OK        | OK         | OK         |
| gcloud            | @RunWith(JUnitParamsRunner::class)        | OK        | OK        | null       | null       |
| flank v20.06.0    | @RunWith(Parameterized::class)            | OK        | OK        | OK         | OK         |
| flank v20.06.0    | @RunWith(Parameterized::class) {named}	| OK        | missing   | OK         | missing    |
| flank v20.06.0    | @RunWith(JUnitParamsRunner::class)        | OK        | missing   | null       | missing    |
| flank v20.06.1    | @RunWith(Parameterized::class)            | OK        | OK        | OK         | OK         |
| flank v20.06.1    | @RunWith(Parameterized::class) {named}	| OK        | OK        | OK         | OK         |
| flank v20.06.1    | @RunWith(JUnitParamsRunner::class)        | OK        | OK        | null       | null       |



# JUnit5
Android instrumented tests has no official support for JUnit5. [There is third-party support](https://github.com/mannodermaus/android-junit5)
