package flank.corellium.adapter

import org.junit.Assert.assertEquals
import org.junit.Test

private const val TEST_APK_PATH = "../../test_artifacts/master/apk/app-single-success-debug-androidTest.apk"

class ParseTestApkTest {

    @Test
    fun test() {
        ParseTestApk(TEST_APK_PATH).run {
            assertEquals(
                "com.example.test_app.test",
                packageName
            )
            assertEquals(
                listOf(
                    "com.example.test_app.InstrumentedTest#ignoredTestWithIgnore",
                    "com.example.test_app.InstrumentedTest#ignoredTestWithSuppress",
                    "com.example.test_app.InstrumentedTest#test",
                ),
                testCases
            )
        }
    }
}
