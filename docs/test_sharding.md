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

Local execution will use the version of Orchestrator defined in your gradle file. Firebase Test Lab currently uses Orchestrator v1.2.0. Firebase must manually upgrade Orchestrator on their devices for customers to use the new version.

[orchestrator]: https://developer.android.com/training/testing/junit-runner#using-android-test-orchestrator
[instrumentation]: https://developer.android.com/reference/android/app/Instrumentation
[androidx_test]: https://github.com/android/android-test/releases/tag/androidx-test-1.3.0-beta02

# GCloud Sharding

The [gcloud CLI][gcloud_cli] supports two types of sharding, uniform sharding and manual sharding via test targets for shard.

[--num-uniform-shards][test_targets_for_shards] is translated to “-e numShard” “-e shardIndex” [AndroidJUnitRunner][ajur] arguments. This uses the native `adb am instrument` sharding feature which randomly shards all tests. When using uniform shards, it's possible to have shards with empty tests.  

Firebase will mark shards as failed when execution consists of skipped/empty tests, as this is likely an indication the tests are not configured correctly. This firebase design choice is incompatable with num-uniform-shards as you will randomly get failures when shards are empty. Using num-uniform-shards is therefor not recommended.

[--test-targets-for-shard][test_targets_for_shards] allows manually specifying tests to be run.

On FTL `--test-targets` or `--test-targets-for-shard` are passed as arguments to `adb shell am instrument`.

> adb shell am instrument -r -w -e class com.example.test_app.ParameterizedTest#shouldHopefullyPass com.example.test_app.test/androidx.test.runner.AndroidJUnitRunner

[gcloud_cli]: https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/android/run
[uniform_shards]: https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/android/run#--num-uniform-shards
[test_targets_for_shards]: https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/android/run#--test-targets-for-shard
[ajur]: https://developer.android.com/training/testing/junit-runner


# Flank

Parameterized tests can be run with Flank by disabling orchestrator and sharding. Flank will omit test targets causing the FTL server to run all available tests.

```
gcloud:
  use-orchestrator: false
  app: ../test_app/apks/app-debug.apk
  test: ../test_app/apks/app-debug-androidTest.apk
  - model: NexusLowRes
    version: 28
    locale: en
    orientation: portrait

flank:
  disable-sharding: false
```

When test targets are specified or sharding is used, then Flank will be limited by tests that are discoverable by [dex-test-parser](https://github.com/linkedin/dex-test-parser).
