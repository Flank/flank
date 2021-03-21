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

class IgnoreFailedIT {
    private val name = this::class.java.simpleName

    @Test
    fun `return with exit code 0 for failed tests`() {
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/flank_android_ignore_failed.yml",
            params = androidRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 0)

        val resOutput = result.output.removeUnicode()
        val outputReport = resOutput.findTestDirectoryFromOutput().toOutputReportFile().json().asOutputReport()

        assertThat(outputReport.error).isEmpty()
        assertThat(outputReport.cost).isNotNull()

        assertThat(outputReport.cost?.physical).isEqualToIgnoringScale(BigDecimal.ZERO)
        assertThat(outputReport.cost?.virtual).isEqualToIgnoringScale("0.02")
        assertThat(outputReport.cost?.total).isEqualTo(outputReport.cost?.virtual)

        assertThat(outputReport.testResults.count()).isEqualTo(1)
        assertThat(outputReport.weblinks.count()).isEqualTo(1)

        val testAxis = outputReport.testResults.values.first().first()
        assertThat(testAxis.outcome).isEqualTo("failure")

        val testSuiteOverview = testAxis.testSuiteOverview

        assertThat(testSuiteOverview.total).isEqualTo(1)
        assertThat(testSuiteOverview.errors).isEqualTo(0)
        assertThat(testSuiteOverview.failures).isEqualTo(1)
        assertThat(testSuiteOverview.flakes).isEqualTo(0)
        assertThat(testSuiteOverview.skipped).isEqualTo(0)
    }
}
