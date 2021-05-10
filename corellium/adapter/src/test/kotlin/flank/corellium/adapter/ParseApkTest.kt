package flank.corellium.adapter

import flank.corellium.api.Apk
import org.junit.Assert.assertEquals
import org.junit.Test

private const val TEST_APK_PATH = "../../test_artifacts/master/apk/app-single-success-debug-androidTest.apk"

class ParseApkTest {

    @Test
    fun parseTestCases() {
        assertEquals(
            listOf(
                "com.example.test_app.InstrumentedTest#ignoredTestWithIgnore",
                "com.example.test_app.InstrumentedTest#ignoredTestWithSuppress",
                "com.example.test_app.InstrumentedTest#test",
            ),
            parseApkTestCases(TEST_APK_PATH)
        )
    }

    @Test
    fun parsePackageName() {
        assertEquals(
            "com.example.test_app.test",
            parseApkPackageName(TEST_APK_PATH)
        )
    }

    @Test
    fun parseInfo() {
        assertEquals(
            Apk.Info(
                packageName = "com.example.test_app.test",
                testRunner = "androidx.test.runner.AndroidJUnitRunner"
            ),
            parseApkInfo(TEST_APK_PATH)
        )
    }
}
