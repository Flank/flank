# Current Flutter status

## Firebase

1. Firebase can run flutter tests. You can find example in ```test_projects/flutter/flutter_example```,
   simple run ```./build_and_run_tests_firebase```.

1. Firebase not supporting sharding for Flutter.

To test sharding on gcloud run

```shell

flutter build apk
dir=$(pwd)
pushd android

./gradlew app:assembleAndroidTest

./gradlew app:assembleDebug -Ptarget=$dir"/integration_tests/integration_tests.dart"

popd

gcloud alpha firebase test android run \
  --project flank-open-source \
  --type instrumentation \
  --app build/app/outputs/apk/debug/app-debug.apk \
  --test build/app/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
  --num-uniform-shards=3 \
  --timeout 5m

```

In this case, gcloud will create 3 shards.

1. One shard will contain all test's
1. Two other shards will be empty.

## Flank

Currently, Flank cannot run any flutter tests.
That happens because  ```app-debug-androidTest.apk``` contains only the test class with the rule. All tests are in ```app-debug.apk```.

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
```shell
./gradlew app:assembleDebug -Ptarget="path to test entry point eg. $dir/integration_tests/integration_tests.dart"
```

All dart tests are placed in ```app-debug.apk``` instead of ```app-debug-androidTest.apk```.
That's why Flank doesn't see flutter tests.

## How flutter tests work in firebase

Flutter Integration Test plugin use ```channel``` to communicate between ```FlutterTestRunner```on android side
and dart code.
When dart tests was finished, flutter send information about test results to native code. Native code
receive information's in ```IntegrationTestPlugin``` in method ```onMethodCall```. ```FlutterTestRunner``` sets test
statuses in method ```run``` (lines: 78, 82, 84).

## Hypothetical solution to allow flank run flutter tests

1. Create a channel to communicate native tests with dart code.

1. Send from native code information about the test to run.

1. Receive the test name and run it on dart code.

1. After the test end send results to native code.

1. Report results.

Probably this solution needs flutter plugin development. We need to create POC to check if it's possible to do. 
