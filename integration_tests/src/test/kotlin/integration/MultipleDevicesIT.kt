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

class MultipleDevicesIT {
    private val name = this::class.java.simpleName

    @Test
    fun `run tests on multiple devices - android`() {
        val name = "$name-android"
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/flank_android_multiple_devices.yml",
            params = androidRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 10)

        val resOutput = result.output.removeUnicode()

        val outputReport = resOutput.findTestDirectoryFromOutput().toOutputReportFile().json().asOutputReport()
        assertThat(outputReport.weblinks.size).isEqualTo(3)
        assertThat(outputReport.error).isEmpty()
        assertThat(outputReport.cost?.virtual).isGreaterThan(BigDecimal.ZERO)
        assertThat(outputReport.cost?.virtual).isEqualToIgnoringScale(outputReport.cost?.total)
        val testsResults = outputReport.testResults
            .map { it.value }
            .flatten()

        assertThat(testsResults.sumBy { it.testSuiteOverview.failures }).isEqualTo(15)
        assertThat(testsResults.sumBy { it.testSuiteOverview.total }).isEqualTo(123)
    }
}
