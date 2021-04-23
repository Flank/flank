package ftl.args

import ftl.test.util.TestHelper
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.StringReader

class ValidateDirectoriesToPullAndroidArgsTest {

    @Test
    fun `should not throw any error with valid directoriesToPull`() {
        // given
        val testYaml = """
            gcloud:
              app: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk
              test: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-single-success-debug-androidTest.apk
              directories-to-pull:
                - /sdcard/data/sample
                - /data/local/tmp/sample
                - /sdcard/
                - /storage
                - /storage/emulated/0/Download
            flank:
              disable-sharding: true
        """.trimIndent()

        // when
        AndroidArgs.load(StringReader(testYaml)).validate()

        // then
        // no issues
    }

    @Test
    fun `should not throw any error without directoriesToPull specific`() {
        // given
        val testYaml = """
            gcloud:
              app: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk
              test: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-single-success-debug-androidTest.apk
            flank:
              disable-sharding: true
        """.trimIndent()

        // when
        AndroidArgs.load(StringReader(testYaml)).validate()

        // then
        // no issues
    }

    @Test
    fun `should throw error with bad prefixed paths in directoriesToPull`() {
        // given
        val expectedErrorMessage = getExpectedMessageForPaths(listOf("/sdcard/data/sample!", "/data/local/tmp/sample#", "/storage/sample@"))
        val testYaml = """
            gcloud:
              app: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk
              test: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-single-success-debug-androidTest.apk
              directories-to-pull:
                - /sdcard/data/sample!
                - /data/local/tmp/sample#
                - /storage/sample@
            flank:
              disable-sharding: true
        """.trimIndent()

        // when
        val exception = TestHelper.getThrowable { AndroidArgs.load(StringReader(testYaml)).validate() }

        // then
        assertEquals(expectedErrorMessage, exception.message)
    }

    @Test
    fun `should throw error with bad characters in paths in directoriesToPull`() {
        // given
        val expectedErrorMessage = getExpectedMessageForPaths(listOf("/app/", "/data/sample"))
        val testYaml = """
            gcloud:
              app: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk
              test: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-single-success-debug-androidTest.apk
              directories-to-pull:
                - /app/
                - /data/sample
                - /sdcard/test
                - /storage/emulated
            flank:
              disable-sharding: true
        """.trimIndent()

        // when
        val exception = TestHelper.getThrowable { AndroidArgs.load(StringReader(testYaml)).validate() }

        // then
        assertEquals(expectedErrorMessage, exception.message)
    }

    private fun getExpectedMessageForPaths(badPaths: List<String>) =
        "Invalid value for [directories-to-pull]: Invalid path $badPaths.\n" +
            "Path must be absolute paths under /sdcard, /storage, or /data/local/tmp (for example, --directories-to-pull /sdcard/tempDir1,/data/local/tmp/tempDir2).\n" +
            "Path names are restricted to the characters [a-zA-Z0-9_-./+]. "
}
