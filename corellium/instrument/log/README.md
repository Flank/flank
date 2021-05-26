# Instrumentation log parser

This module is providing a tool for parsing raw console log from the `adb shell am instrument -r` command into data the structures.

### References

* Module type - [tool](../../../docs/architecture.md#tool)
* Dependency type - [static](../../../docs/architecture.md#static_dependencies)
* Public API - [Parser.kt](./src/main/kotlin/flank/instrument/log/Parser.kt)
* Example console log - [0](./src/test/resources/example_android_logs_0.txt) [1](./src/test/resources/example_android_logs_1.txt) [2](./src/test/resources/example_android_logs_2.txt) [3](./src/test/resources/example_android_logs_3.txt)

### Example input

The stream of lines:

```
<st_app.test/androidx.test.runner.AndroidJUnitRunner
INSTRUMENTATION_STATUS: class=com.example.test_app.InstrumentedTest
INSTRUMENTATION_STATUS: current=1
INSTRUMENTATION_STATUS: id=AndroidJUnitRunner
INSTRUMENTATION_STATUS: numtests=2
INSTRUMENTATION_STATUS: stream=
com.example.test_app.InstrumentedTest:
INSTRUMENTATION_STATUS: test=test
INSTRUMENTATION_STATUS_CODE: 1
INSTRUMENTATION_STATUS: class=com.example.test_app.InstrumentedTest
INSTRUMENTATION_STATUS: current=1
INSTRUMENTATION_STATUS: id=AndroidJUnitRunner
INSTRUMENTATION_STATUS: numtests=2
INSTRUMENTATION_STATUS: stream=.
INSTRUMENTATION_STATUS: test=test
INSTRUMENTATION_STATUS_CODE: 0
INSTRUMENTATION_STATUS: class=com.example.test_app.InstrumentedTest
INSTRUMENTATION_STATUS: current=2
INSTRUMENTATION_STATUS: id=AndroidJUnitRunner
INSTRUMENTATION_STATUS: numtests=2
INSTRUMENTATION_STATUS: stream=
INSTRUMENTATION_STATUS: test=ignoredTestWithIgnore
INSTRUMENTATION_STATUS_CODE: 1
INSTRUMENTATION_STATUS: class=com.example.test_app.InstrumentedTest
INSTRUMENTATION_STATUS: current=2
INSTRUMENTATION_STATUS: id=AndroidJUnitRunner
INSTRUMENTATION_STATUS: numtests=2
INSTRUMENTATION_STATUS: stream=
INSTRUMENTATION_STATUS: test=ignoredTestWithIgnore
INSTRUMENTATION_STATUS_CODE: -3
INSTRUMENTATION_RESULT: stream=

Time: 1.576

OK (1 test)


INSTRUMENTATION_CODE: -1
```

### Example output

```kotlin
flowOf(
    Status(
        code = 0,
        startTime = 1621956912031,
        endTime = 1621956912032,
        details = Details(
            raw = {
                "test" = "test"
                "stream" = ["", "com.example.test_app.InstrumentedTest:", "."]
                "numtests" = 2
                "id" = "AndroidJUnitRunner"
                "current" = 1
                "class" = "com.example.test_app.InstrumentedTest"
            },
            className = com.example.test_app.InstrumentedTest,
            testName = test,
            stack = null
        )
    ),
    Status(
        code = -3,
        startTime = 1621956912048,
        endTime = 1621956912048,
        details = Details(
            raw = {
                "test" = "ignoredTestWithIgnore"
                "stream" = ["", ""]
                "numtests" = 2
                "id" = "AndroidJUnitRunner"
                "current" = 2
                "class" = "com.example.test_app.InstrumentedTest"
            },
            className = com.example.test_app.InstrumentedTest,
            testName = ignoredTestWithIgnore,
            stack = null
        )
    ),
    Result(
        code = -1,
        time = 1621956912048,
        details = {
            "stream" = ["", "\n", "Time: 1.576", "\n", "OK (1 test)", "\n", "\n"]
        }
    ),
)
```

The `startTime` and `endTime` values are evaluated during the parsing, so it's important to parse logs directly from the console which is producing log lines. Otherwise, the `startTime` and `endTime` will not be valid.
