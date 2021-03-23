# Current Flutter status

Investigation of flutter-android support on gcloud including test sharding.

# Gcloud

In the gcloud we can use following commands to run tests in shards:

* [`--num-uniform-shards`](https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/android/run#--num-uniform-shards)
* [`--test-targets-for-shard`](https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/android/run#--test-targets-for-shard)
* [`--test-targets`](https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/android/run#--test-targets) - this option is not designed directly for sharding but filtering

## Preparing flutter app

All following tests require a flutter app for testing that can be build using following script:

```shell
flutter build apk
dir=$(pwd)
pushd android

./gradlew app:assembleAndroidTest

./gradlew app:assembleDebug -Ptarget=$dir"/integration_tests/integration_tests.dart"

popd
```

## --num-uniform-shards

Test:

```shell
gcloud alpha firebase test android run \
  --project flank-open-source \
  --type instrumentation \
  --app build/app/outputs/apk/debug/app-debug.apk \
  --test build/app/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
  --num-uniform-shards=3 \
  --timeout 5m
```

Result:

```shell
┌─────────┬────────────────────────┬───────────────────────────────┐
│ OUTCOME │    TEST_AXIS_VALUE     │          TEST_DETAILS         │
├─────────┼────────────────────────┼───────────────────────────────┤
│ Failed  │ walleye-27-en-portrait │ 1 test cases failed, 5 passed │
└─────────┴────────────────────────┴───────────────────────────────┘
```

#### Expected behaviour

The Flutter example app contains 6 test methods, so according to doc, 
the gcloud should create 3 shards, 
each shard should contain 2 methods.

#### Investigation results

* One shard will contain all test's
* Two other shards will be empty.

#### Conclusions

The result is different from expected behaviour.

## --test-targets-for-shard

Using this option you can specify shards targets by:

* `metod` - test method name
* `class` - test class name
* `package` - test package name
* `annotation`

### Sharding by method

Test:

```shell
gcloud alpha firebase test android run \
  --project flank-open-source \
  --type instrumentation \
  --app build/app/outputs/apk/debug/app-debug.apk \
  --test build/app/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
  --test-targets-for-shard "class org.flank.flutter_example.MainActivityTest#success_test_example_5" \
  --timeout 5m
```

Where:
```success_test_example_5``` is a [dart test](https://github.com/Flank/flank/blob/master/test_projects/flutter/flutter_example/integration_tests/success_test.dart#L78).

Result:

```shell
┌─────────┬────────────────────────┬────────────────────┐
│ OUTCOME │    TEST_AXIS_VALUE     │    TEST_DETAILS    │
├─────────┼────────────────────────┼────────────────────┤
│ Failed  │ walleye-27-en-portrait │ Test failed to run │
└─────────┴────────────────────────┴────────────────────┘
```

#### Expected behaviour

The gcloud should run only one shard with one test
method: `org.flank.flutter_example.MainActivityTest#success_test_example_5`

#### Investigation results

Gcloud is returning `Test failed to run` as test details, no test are being run.

#### Conclusions

* gcloud can't find dart tests.
* gcloud will create an empty shard.

### Sharding by class or package

Test:

```shell
gcloud alpha firebase test android run \
  --project flank-open-source \
  --type instrumentation \
  --app build/app/outputs/apk/debug/app-debug.apk \
  --test build/app/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
  --test-targets-for-shard "class org.flank.flutter_example.MainActivityTest" \
  --timeout 5m
```

or

```shell
gcloud alpha firebase test android run \
  --project flank-open-source \
  --type instrumentation \
  --app build/app/outputs/apk/debug/app-debug.apk \
  --test build/app/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
  --test-targets-for-shard "package org.flank.flutter_example" \
  --timeout 5m
```

Result:

```shell
┌─────────┬────────────────────────┬───────────────────────────────┐
│ OUTCOME │    TEST_AXIS_VALUE     │          TEST_DETAILS         │
├─────────┼────────────────────────┼───────────────────────────────┤
│ Failed  │ walleye-27-en-portrait │ 1 test cases failed, 5 passed │
└─────────┴────────────────────────┴───────────────────────────────┘
```

#### Expected behaviour

The gcloud should run all tests, one tests is failing intentionally.

#### Investigation results

Test results are same as expected.

#### Conclusions

The gcloud can run tests by class or package if there are exists in test apk as normal android test.

## --test-targets

This option works similar to Using this option you can filter test cases for running only the specified units:

* `metod` - test method name
* `class` - test class name
* `package` - test package name
* `annotation`

### Filtering by method:

Test:

```shell
gcloud alpha firebase test android run \
  --project flank-open-source \
  --type instrumentation \
  --app build/app/outputs/apk/debug/app-debug.apk \
  --test build/app/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
  --test-targets "class org.flank.flutter_example.MainActivityTest#success_test_example_5" \
  --timeout 5m
```

Result:

```shell
┌─────────┬────────────────────────┬────────────────────┐
│ OUTCOME │    TEST_AXIS_VALUE     │    TEST_DETAILS    │
├─────────┼────────────────────────┼────────────────────┤
│ Failed  │ walleye-27-en-portrait │ Test failed to run │
└─────────┴────────────────────────┴────────────────────┘

```

### Filtering by class or package:

Test:

```shell
gcloud alpha firebase test android run \
  --project flank-open-source \
  --type instrumentation \
  --app build/app/outputs/apk/debug/app-debug.apk \
  --test build/app/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
  --test-targets "class org.flank.flutter_example.MainActivityTest" \
  --timeout 5m
```

or

```shell
gcloud alpha firebase test android run \
  --project flank-open-source \
  --type instrumentation \
  --app build/app/outputs/apk/debug/app-debug.apk \
  --test build/app/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
  --test-targets "package org.flank.flutter_example" \
  --timeout 5m
```

Result:

```shell
┌─────────┬────────────────────────┬───────────────────────────────┐
│ OUTCOME │    TEST_AXIS_VALUE     │          TEST_DETAILS         │
├─────────┼────────────────────────┼───────────────────────────────┤
│ Failed  │ walleye-27-en-portrait │ 1 test cases failed, 5 passed │
└─────────┴────────────────────────┴───────────────────────────────┘

```

#### Expected behaviour

The gcloud should run all tests, one tests is failing intentionally.

#### Investigation results

Test results are same as expected.

#### Conclusions

The gcloud can filter tests by class or package if there are exists in test apk as normal android test.

### Summary conclusion

* Gcloud can run flutter tests without sharding. You can find example
  in [flutter_example](https://github.com/Flank/flank/blob/master/test_projects/flutter/flutter_example), simple
  run [build_and_run_tests_firebase.sh](https://github.com/Flank/flank/blob/master/test_projects/flutter/flutter_example/build_and_run_tests_firebase.sh).

* Gcloud is not supporting sharding for Flutter, because all `dart` tests according to
  the [flutter integration tests doc](https://pub.dev/packages/integration_test#android-device-testing) are hidden
  behind [android test class](https://github.com/Flank/flank/blob/master/test_projects/flutter/flutter_example/android/app/src/androidTest/java/org/flank/flutter_example/MainActivityTest.java)
  and are not visible for gcloud.

## Flank

Currently, Flank cannot run any flutter tests. That happens because  ```app-debug-androidTest.apk``` contains only the
test class with the rule. All tests are in ```app-debug.apk```.

## How to create flutter tests for firebase

1. Android side

```java

@RunWith(FlutterTestRunner.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, false);
}

```

1. Flutter side

This is the test app entry point.

```dart

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  testWidgets("success test example", (WidgetTester tester) async {
    // Wait for app widget
    await tester.pumpWidget(MyApp());

    // Verify that our counter starts at 0.
    expect(find.text('0'), findsOneWidget);
    expect(find.text('1'), findsNothing);

    // Tap the '+' icon and trigger a frame.
    await tester.tap(find.byIcon(Icons.add));
    await tester.pump();

    // Verify that our counter has incremented.
    expect(find.text('0'), findsNothing);
    expect(find.text('1'), findsOneWidget);
  });
}

```

You need to compile the Flutter app with few steps

```shell
flutter build apk
```

```shell
./gradlew app:assembleAndroidTest
```

```shell
./gradlew app:assembleDebug -Ptarget="path to test entry point eg. $dir/integration_tests/integration_tests.dart"
```

All dart tests are placed in ```app-debug.apk``` instead of ```app-debug-androidTest.apk```. That's why Flank doesn't
see flutter tests.

## How flutter tests work in firebase

Flutter Integration Test plugin use ```channel``` to communicate between ```FlutterTestRunner```on android side and dart code. 
When dart tests was finished, flutter send information about test results to native code. 
Native code receive information's in [```IntegrationTestPlugin.onMethodCall()```](https://github.com/flutter/plugins/blob/7b9ac6b0c20da2ae3cdbaf4f2a06a9b9eb6e1474/packages/integration_test/android/src/main/java/dev/flutter/plugins/integration_test/IntegrationTestPlugin.java#L52). 
```FlutterTestRunner``` sets test statuses in method [```FlutterTestRunner.run()```](https://github.com/flutter/plugins/blob/7b9ac6b0c20da2ae3cdbaf4f2a06a9b9eb6e1474/packages/integration_test/android/src/main/java/dev/flutter/plugins/integration_test/FlutterTestRunner.java#L55) in lines:

1. [```notifier.fireTestStarted(d);```](https://github.com/flutter/plugins/blob/7b9ac6b0c20da2ae3cdbaf4f2a06a9b9eb6e1474/packages/integration_test/android/src/main/java/dev/flutter/plugins/integration_test/FlutterTestRunner.java#L78)
1. [```notifier.fireTestFailure(new Failure(d, dummyException));```](https://github.com/flutter/plugins/blob/7b9ac6b0c20da2ae3cdbaf4f2a06a9b9eb6e1474/packages/integration_test/android/src/main/java/dev/flutter/plugins/integration_test/FlutterTestRunner.java#L82)
1. [```notifier.fireTestFinished(d);```](https://github.com/flutter/plugins/blob/7b9ac6b0c20da2ae3cdbaf4f2a06a9b9eb6e1474/packages/integration_test/android/src/main/java/dev/flutter/plugins/integration_test/FlutterTestRunner.java#L84)

## Hypothetical solution to allow flank run flutter tests

1. Create a channel to communicate native tests with dart code.

1. Send from native code information about the test to run.

1. Receive the test name and run it on dart code.

1. After the test end send results to native code.

1. Report results.

Probably this solution needs flutter plugin development. We need to create POC to check if it's possible to do. 
