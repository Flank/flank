package flank.apk

import org.junit.Assert.assertEquals
import org.junit.Test

private const val TEST_APK_PATH = "../../test_artifacts/master/apk/app-multiple-success-debug-androidTest.apk"

class ParseApkTest {

    private val apk = Apk.Api()

    /**
     * Parse test cases with following filters conditions:
     *
     * 1. Classes annotated by @RunWith(JUnitParamsRunner::class) wouldn't be split into test methods but delivered as class.
     * 2. Filter out test case named test0 and test cases annotated with org.junit.Ignore
     */
    @Test
    fun parseTestCases() {
        val config = Apk.ParseTestCases.Config(
            filterClass = setOf(Apk.Runner.JUnitParamsRunner),
            filterTest = { (name, annotations) ->
                println("$name $annotations")
                name != "com.example.test_app.InstrumentedTest#test0" && !annotations.contains("org.junit.Ignore")
            }
        )

        assertEquals(
            listOf(
                "com.example.test_app.InstrumentedTest#ignoredTestWitSuppress",
                // "com.example.test_app.InstrumentedTest#ignoredTestWithIgnore",
                // "com.example.test_app.InstrumentedTest#test0",
                "com.example.test_app.InstrumentedTest#test1",
                "com.example.test_app.InstrumentedTest#test2",
                "com.example.test_app.ParameterizedTest#shouldHopefullyPass",
                // "com.example.test_app.bar.BarInstrumentedTest#ignoredTestBar",
                "com.example.test_app.bar.BarInstrumentedTest#testBar",
                // "com.example.test_app.foo.FooInstrumentedTest#ignoredTestFoo",
                "com.example.test_app.foo.FooInstrumentedTest#testFoo",
                "com.example.test_app.parametrized.EspressoParametrizedClassParameterizedNamed#clickRightButton",
                "com.example.test_app.parametrized.EspressoParametrizedClassTestParameterized#clickRightButton",
                "com.example.test_app.parametrized.EspressoParametrizedMethodTestJUnitParamsRunner",
            ),
            apk.parseTestCases(config)(TEST_APK_PATH)
        )
    }

    @Test
    fun parsePackageName() {
        assertEquals(
            "com.example.test_app.test",
            apk.parsePackageName(TEST_APK_PATH)
        )
    }

    @Test
    fun parseInfo() {
        assertEquals(
            Apk.Info(
                packageName = "com.example.test_app.test",
                testRunner = "androidx.test.runner.AndroidJUnitRunner"
            ),
            apk.parseInfo(TEST_APK_PATH)
        )
    }
}
