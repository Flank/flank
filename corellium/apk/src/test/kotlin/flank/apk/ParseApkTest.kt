package flank.apk

import org.junit.Assert.assertEquals
import org.junit.Test

private const val TEST_APK_PATH = "../../test_artifacts/master/apk/app-single-success-debug-androidTest.apk"

class ParseApkTest {

    private val apk = Apk.Api()

    @Test
    fun parseTestCases() {
        assertEquals(
            listOf(
                "com.example.test_app.InstrumentedTest#ignoredTestWithIgnore",
                "com.example.test_app.InstrumentedTest#ignoredTestWithSuppress",
                "com.example.test_app.InstrumentedTest#test",
            ),
            apk.parseTestCases(TEST_APK_PATH)
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
