package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import run
import utils.asOutputReport
import utils.findTestDirectoryFromOutput
import utils.json
import utils.toOutputReportFile
import java.math.BigDecimal

class TestFilteringIT {
    private val name = this::class.java.simpleName

    @Test
    fun `run test from only one apk`() {
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/test_filtering_android.yml",
            params = androidRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 0)

        val resOutput = result.output.removeUnicode()
        val outputReport = resOutput.findTestDirectoryFromOutput().toOutputReportFile().json().asOutputReport()

        assertThat(outputReport.cost).isNotNull()
        assertThat(outputReport.weblinks).isNotEmpty()
        assertThat(outputReport.error).isEmpty()
        assertThat(outputReport.cost?.virtual).isGreaterThan(BigDecimal.ZERO)

        val testSuiteOverview = outputReport.testResults.values.first().first().testSuiteOverview
        assertThat(testSuiteOverview.total).isEqualTo(1)
        assertThat(testSuiteOverview.errors).isEqualTo(0)
        assertThat(testSuiteOverview.failures).isEqualTo(0)
        assertThat(testSuiteOverview.flakes).isEqualTo(0)
        assertThat(testSuiteOverview.skipped).isEqualTo(0)
    }
}
