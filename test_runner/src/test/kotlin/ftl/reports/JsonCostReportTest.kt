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
class JsonCostReportTest {

    @Test
    fun `run fromEmptyResult`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/empty_result", AndroidArgs.default())
        val mockArgs = mockk<AndroidArgs>(relaxed = true)
        JsonCostReport.run(matrix, null, printToStdout = false, args = mockArgs)

        val actualReport = File("./src/test/kotlin/ftl/fixtures/empty_result/CostReport.json").readText()
        val expectedReport = """
{
  "currency": "USD",
  "cost": {
    "virtual": 0.02,
    "physical": 0.00,
    "total": 0.02
  },
  "billable-minutes": {
    "virtual": 1,
    "physical": 0,
    "total": 1
  }
}
""".trim()

        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun `run fromErrorResult`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/error_result", AndroidArgs.default())
        val mockArgs = mockk<AndroidArgs>(relaxed = true)
        JsonCostReport.run(matrix, null, printToStdout = false, args = mockArgs)

        val actualReport = File("./src/test/kotlin/ftl/fixtures/error_result/CostReport.json").readText()
        val expectedReport = """
{
  "currency": "USD",
  "cost": {
    "virtual": 0.07,
    "physical": 0.25,
    "total": 0.32
  },
  "billable-minutes": {
    "virtual": 4,
    "physical": 3,
    "total": 7
  }
}
""".trim()

        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun `run fromIosExitCode`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/ios_exit_code", AndroidArgs.default())
        val mockArgs = mockk<AndroidArgs>(relaxed = true)
        JsonCostReport.run(matrix, null, printToStdout = false, args = mockArgs)

        val actualReport = File("./src/test/kotlin/ftl/fixtures/ios_exit_code/CostReport.json").readText()
        val expectedReport = """
{
  "currency": "USD",
  "cost": {
    "virtual": 0.00,
    "physical": 0.33,
    "total": 0.33
  },
  "billable-minutes": {
    "virtual": 0,
    "physical": 4,
    "total": 4
  }
}
""".trim()

        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun `run fromSuccessResult`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/success_result", AndroidArgs.default())
        val mockArgs = mockk<AndroidArgs>(relaxed = true)
        JsonCostReport.run(matrix, null, printToStdout = false, args = mockArgs)

        val actualReport = File("./src/test/kotlin/ftl/fixtures/success_result/CostReport.json").readText()
        val expectedReport = """
{
  "currency": "USD",
  "cost": {
    "virtual": 0.02,
    "physical": 0.00,
    "total": 0.02
  },
  "billable-minutes": {
    "virtual": 1,
    "physical": 0,
    "total": 1
  }
}
""".trim()

        assertThat(actualReport).isEqualTo(expectedReport)
    }
}
