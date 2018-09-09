package ftl.reports.utils

import ftl.reports.util.ReportManager
import ftl.run.TestRunner
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

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
        ReportManager.generate(matrix)
    }

    @Test
    fun generate_fromSuccessResult() {
        val matrix = TestRunner.matrixPathToObj("./src/test/kotlin/ftl/fixtures/success_result")
        ReportManager.generate(matrix)
    }
}
