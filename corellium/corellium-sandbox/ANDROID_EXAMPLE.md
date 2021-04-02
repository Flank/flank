# Android <> Corellium example

## Flank test run with Corellium instances (Android)

### 1. ADB + credentials

Ensure you have `adb` installed and added to the `PATH`.

Add credentials to the [properties file](./src/main/resources/corellium-config.properties) (if properties file is missing, please run `./gradlew tasks` - it will create it automatically), required:
* api
* username
* password

### 2. Start VPN

Setup described [here](./README.md#vpn)

### 3. Run example

Run example with gradle task `./gradlew runAndroidExample`

#### What example script does?

1. log in
2. find project `Default Project` and fetch its info
3. look for instance with `instance_name` (see properties file)
4. if there is no such instance -- create a new one (it takes some time)
5. wait until the instance is ready
6. create and connect an agent
7. wait until the agent is ready to use
8. install apks (`adb install [path to apk]`, `adb install -t [path to test apk]`)
9. start instrumentation test:
    ```shell
    adb shell am instrument -w com.example.test_app.test/androidx.test.runner.AndroidJUnitRunner 
    ```
10. uninstall apks after run is finished

### 4. Enjoy instrumented test on corellium device

Example logs:
```shell
Looking for corellium-android instance
Wait until instance is ready (may take some time ~3-5 min)
Progress
.

Creating agent
Await agent is connected and ready to use
Progress
.........
Received: {"id":1,"success":true}

Agent ready
already connected to 10.11.1.2:5001
List of devices attached
10.11.1.2:5001	device

Performing Streamed Install
Success
Performing Streamed Install
Success
Running instrumentation tests:
  app: ./corellium/corellium-sandbox/src/main/resources/android/app-debug.apk
  test: ./corellium/corellium-sandbox/src/main/resources/android/app-multiple-success-debug-androidTest.apk

com.example.test_app.InstrumentedTest:
com.example.test_app.InstrumentedTest:...
com.example.test_app.ParameterizedTest:...
com.example.test_app.bar.BarInstrumentedTest:.
com.example.test_app.foo.FooInstrumentedTest:.
com.example.test_app.parametrized.EspressoParametrizedClassParameterizedNamed:...
com.example.test_app.parametrized.EspressoParametrizedClassTestParameterized:...
com.example.test_app.parametrized.EspressoParametrizedMethodTestJUnitParamsRunner:......
com.example.test_app.similar.SimilarNameTest10:...
com.example.test_app.similar.SimilarNameTest1:...

Time: 21.266

OK (26 tests)


Cleaning up...
Success
Success
```
