## Cukeulator Example Application
This is the example test-project for the Cukeulator app for Android Studio 3.0+

### Setup
Features must be placed in `androidTest/assets/features/`. Subdirectories are allowed.

The rest of the dependencies are added automatically in `cukeulator/build.gradle`.

The cucumber-android dependency is added as (see `cukeulator/build.gradle`):

```
androidTestImplementation 'io.cucumber:cucumber-android:<version>'
```

### Using gradle
To build the cukeulator apk:
```
./gradlew --parallel :cukeulator:assemble
```
The build generates an apk in cukeulator/build/outputs/apk/debug/cukeulator-debug.apk.


To build the instrumentation test apk:
```
./gradlew --parallel :cukeulator:assembleDebugTest
```

To install the apk on a device:
```
adb install -r cukeulator/build/outputs/apk/debug/cukeulator-debug.apk
```

To install the test apk on a device:
```
adb install -r cukeulator/build/outputs/apk/androidTest/debug/cukeulator-debug-androidTest.apk
```

To verify that the test is installed, run:

```
adb shell pm list instrumentation
```

The command output should display;

```
instrumentation:cucumber.cukeulator.test/.CukeulatorAndroidJUnitRunner (target=cucumber.cukeulator)
```

To run the test:

```
./gradlew :cukeulator:connectedCheck
```

As an alternative option, the test can be run with adb:

```
adb shell am instrument -w cucumber.cukeulator.test/cucumber.cukeulator.test.CukeulatorAndroidJUnitRunner
```

### Using an Android Studio IDE
1. Import the example to Android Studio: `File > Import Project`.
2. Create a test run configuration:
    1.  Run > Edit Configurations
    2. Click `+` button and select Android Instrumented Tests
    3. Specify test name: `CalculatorTest`
    4. Select module: `cukeulator`
    5. Enter a Specific instrumentation runner: `cucumber.cukeulator.test/cucumber.cukeulator.test.CukeulatorAndroidJUnitRunner`
    6. Click Ok

### Output
Filter for the logcat tag `cucumber-android` in [DDMS](https://developer.android.com/tools/debugging/ddms.html).

### Using this project with locally built Cucumber-JVM
See [cukeulator/build.gradle](build.gradle) under `dependencies`.  
There is a source-code comment which explains how to use a locally built Cucumber-JVM Android library.
