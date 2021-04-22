package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import integration.config.AndroidTest
import org.junit.Test
import org.junit.experimental.categories.Category
import run
import utils.CONFIGS_PATH
import utils.FLANK_JAR_PATH
import utils.androidRunCommands
import utils.asOutputReport
import utils.assertCostMatches
import utils.assertExitCode
import utils.findTestDirectoryFromOutput
import utils.json
import utils.removeUnicode
import utils.toOutputReportFile

class MultipleDevicesIT {
    private val name = this::class.java.simpleName

    @Category(AndroidTest::class)
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

        outputReport.assertCostMatches()

        val testsResults = outputReport.testResults
            .map { it.value }
            .map { it.testAxises }
            .flatten()

        assertThat(testsResults.sumBy { it.testSuiteOverview.failures }).isEqualTo(15)
        assertThat(testsResults.sumBy { it.testSuiteOverview.total }).isEqualTo(123)
    }
}
