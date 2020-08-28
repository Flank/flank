package ftl.args

import ftl.gc.GcStorage
import ftl.gc.GcStorage.exist
import ftl.test.util.TestHelper
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.StringReader

class ValidateResultsDirUniqueTests {

    @Test
    fun `should not throw any error when path not exist in GCloud`() {
        // given
        val testYaml = """
            gcloud:
              app: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk
              test: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-single-success-debug-androidTest.apk
              results-dir: flank
            flank:
              disable-sharding: true
        """.trimIndent()

        // when
        mockkObject(GcStorage) {
            every { exist(any(), any()) } returns false
            AndroidArgs.load(StringReader(testYaml)).validate()
        }

        // then
        // no issues
    }

    @Test
    fun `should throw error when path already exist in GCloud`() {
        // given
        val expectedErrorMessage = "Google cloud storage result directory should be unique."
        val testYaml = """
            gcloud:
              app: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk
              test: ./src/test/kotlin/ftl/fixtures/tmp/apk/app-single-success-debug-androidTest.apk
              results-dir: flank
            flank:
              disable-sharding: true
        """.trimIndent()
        var actualException: Throwable? = null

        // when
        mockkObject(GcStorage) {
            every { exist(any(), any()) } returns true
            actualException = TestHelper.getThrowable { AndroidArgs.load(StringReader(testYaml)).validate() }
        }
        // then
        assertEquals(expectedErrorMessage, actualException?.message)
    }
}
