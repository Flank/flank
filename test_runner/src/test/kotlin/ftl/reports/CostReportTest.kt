package ftl.reports

import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.run.common.matrixPathToObj
import ftl.test.util.FlankTestRunner
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(FlankTestRunner::class)
class CostReportTest {

    @Test
    fun `run fromEmptyResult`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/empty_result", AndroidArgs.default())
        val mockArgs = mockk<AndroidArgs>(relaxed = true)
        CostReport.run(matrix, null, printToStdout = false, args = mockArgs)

        val actualReport = File("./src/test/kotlin/ftl/fixtures/empty_result/CostReport.txt").readText()
        val expectedReport = """
CostReport
  Virtual devices
    $0.02 for 1m

""".trimStart()

        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun `run fromErrorResult`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/error_result", AndroidArgs.default())
        val mockArgs = mockk<AndroidArgs>(relaxed = true)
        CostReport.run(matrix, null, printToStdout = false, args = mockArgs)

        val actualReport = File("./src/test/kotlin/ftl/fixtures/error_result/CostReport.txt").readText()
        val expectedReport = """
CostReport
  Physical devices
    $0.25 for 3m
  
  Virtual devices
    $0.07 for 4m
  
  Total
    $0.32 for 7m

""".trimStart()

        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun `run fromIosExitCode`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/ios_exit_code", AndroidArgs.default())
        val mockArgs = mockk<AndroidArgs>(relaxed = true)
        CostReport.run(matrix, null, printToStdout = false, args = mockArgs)

        val actualReport = File("./src/test/kotlin/ftl/fixtures/ios_exit_code/CostReport.txt").readText()
        val expectedReport = """
CostReport
  Physical devices
    $0.33 for 4m

""".trimStart()

        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun `run fromSuccessResult`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/success_result", AndroidArgs.default())
        val mockArgs = mockk<AndroidArgs>(relaxed = true)
        CostReport.run(matrix, null, printToStdout = false, args = mockArgs)

        val actualReport = File("./src/test/kotlin/ftl/fixtures/success_result/CostReport.txt").readText()
        val expectedReport = """
CostReport
  Virtual devices
    $0.02 for 1m

""".trimStart()

        assertThat(actualReport).isEqualTo(expectedReport)
    }
}
