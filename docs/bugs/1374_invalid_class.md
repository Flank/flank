# [Parametrized tests] #1374 - java.lang.ClassNotFoundException: Invalid name: 

## Bug description

Test code with parametrized code and custom name

```kotlin
@RunWith(Parameterized::class)
class BrokenTestName2(private val testCase: TestCase) {

    @Test fun test() {
        val expectedRemainder = if (testCase.odd) 1 else 0
        for (value in testCase.values) {
            assertEquals(expectedRemainder, abs(value % 2))
        }
    }

    data class TestCase(
        val values: List<Int>,
        val odd: Boolean,
    )

    companion object {

        @[JvmStatic Parameterized.Parameters(name = "{0}")]
        fun parameters(): List<TestCase> = listOf(
            TestCase(listOf(-4, -2, 0, 2, 4), odd = false),
            TestCase(listOf(-3, -5, -1, 1, 3, 5), odd = true),
        )
    }
}
```

Will produce failures such as:
```
java.lang.ClassNotFoundException: Invalid name:  3
at java.lang.Class.classForName(Native Method)
at java.lang.Class.forName(Class.java:324)
at androidx.test.internal.runner.TestLoader.doCreateRunner(TestLoader.java:72)
at androidx.test.internal.runner.TestLoader.getRunnersFor(TestLoader.java:105)
at androidx.test.internal.runner.TestRequestBuilder.build(TestRequestBuilder.java:804)
at androidx.test.runner.AndroidJUnitRunner.buildRequest(AndroidJUnitRunner.java:575)
at androidx.test.runner.AndroidJUnitRunner.onStart(AndroidJUnitRunner.java:393)
at android.app.Instrumentation$InstrumentationThread.run(Instrumentation.java:1879)
```

and similar failures for other values interspersed with commas, e.g. `java.lang.ClassNotFoundException: Invalid name:  -2`, etc.

Such failures appear like this in the FTL UI:

![image](https://user-images.githubusercontent.com/2455337/101087202-bedca680-3566-11eb-918a-c34b123e803d.png)

## Test details

No matter if sharding is disabled or not, tests will always fail both on GCloud and Flank

## Solution

Disable test orchestrator with yaml option:
```yaml
gcloud:
  ...
  use-orchestrator: false
  ...
flank:
  ...
```
or CLI command:
```
--no-use-orchestrator
```

## More info

Please take a look at [documentation about sharding](https://github.com/Flank/flank/blob/master/docs/test_sharding.md#parameterized-tests) for more info
