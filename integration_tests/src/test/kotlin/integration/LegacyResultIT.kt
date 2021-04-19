package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import flank.common.isWindows
import org.junit.Assume.assumeFalse
import org.junit.Test
import run
import utils.CONFIGS_PATH
import utils.FLANK_JAR_PATH
import utils.androidRunCommands
import utils.asOutputReport
import utils.assertCostMatches
import utils.assertExitCode
import utils.assertTestCountMatches
import utils.findTestDirectoryFromOutput
import utils.firstTestSuiteOverview
import utils.iosRunCommands
import utils.json
import utils.removeUnicode
import utils.toJUnitXmlFile
import utils.toOutputReportFile

class LegacyResultIT {
    private val name = this::class.java.simpleName

    @Test
    fun androidLegacyJUnitResultTest() {
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/flank_android_single_legacy.yml",
            params = androidRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 0)

        val resOutput = result.output.removeUnicode()
        val resultDirectory = resOutput.findTestDirectoryFromOutput()
        assertThat(resultDirectory.toJUnitXmlFile().exists()).isTrue()

        val outputReport = resultDirectory.toOutputReportFile().json().asOutputReport()

        assertThat(outputReport.error).isEmpty()
        assertThat(outputReport.cost).isNotNull()

        outputReport.assertCostMatches()

        assertThat(outputReport.testResults.count()).isEqualTo(1)
        assertThat(outputReport.weblinks.count()).isEqualTo(1)

        val testSuiteOverview = outputReport.firstTestSuiteOverview

        testSuiteOverview.assertTestCountMatches(
            total = 2,
            skipped = 1
        )
    }

    @Test
    fun iosLegacyJUnitResultTest() {
        assumeFalse(isWindows)
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/flank_ios_single_legacy.yml",
            params = iosRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 0)

        val resOutput = result.output.removeUnicode()
        val resultDirectory = resOutput.findTestDirectoryFromOutput()
        assertThat(resultDirectory.toJUnitXmlFile().exists()).isTrue()

        val outputReport = resultDirectory.toOutputReportFile().json().asOutputReport()

        assertThat(outputReport.error).isEmpty()
        assertThat(outputReport.cost).isNotNull()

        assertThat(outputReport.testResults.count()).isEqualTo(1)
        assertThat(outputReport.weblinks.count()).isEqualTo(1)

        val testSuiteOverview = outputReport.firstTestSuiteOverview

        testSuiteOverview.assertTestCountMatches(
            total = 17,
            skipped = 0
        )
    }
}
