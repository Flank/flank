package ftl.reports.utils

import ftl.args.AndroidArgs
import ftl.reports.util.ReportManager
import ftl.run.TestRunner
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(FlankTestRunner::class)
class ReportManagerTest {

    @Rule
    @JvmField
    val systemErrRule = SystemErrRule().muteForSuccessfulTests()!!

    @Rule
    @JvmField
    val systemOutRule = SystemOutRule().muteForSuccessfulTests()!!

    @Test
    fun generate_fromErrorResult() {
        val matrix = TestRunner.matrixPathToObj("./src/test/kotlin/ftl/fixtures/error_result")
        val mockArgs = mock(AndroidArgs::class.java)
        `when`(mockArgs.smartFlankGcsPath).thenReturn("")
        ReportManager.generate(matrix, mockArgs)
    }

    @Test
    fun generate_fromSuccessResult() {
        val matrix = TestRunner.matrixPathToObj("./src/test/kotlin/ftl/fixtures/success_result")
        val mockArgs = mock(AndroidArgs::class.java)
        `when`(mockArgs.smartFlankGcsPath).thenReturn("")
        ReportManager.generate(matrix, mockArgs)
    }
}
