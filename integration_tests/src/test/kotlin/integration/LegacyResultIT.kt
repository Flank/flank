package integration

import FlankCommand
import com.google.common.truth.Truth
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
        Truth.assertThat(resultDirectory.toJUnitXmlFile().exists()).isTrue()

        val outputReport = resultDirectory.toOutputReportFile().json().asOutputReport()

        Truth.assertThat(outputReport.error).isEmpty()
        Truth.assertThat(outputReport.cost).isNotNull()

        outputReport.assertCostMatches()

        Truth.assertThat(outputReport.testResults.count()).isEqualTo(1)
        Truth.assertThat(outputReport.weblinks.count()).isEqualTo(1)

        val testSuiteOverview = outputReport.firstTestSuiteOverview

        testSuiteOverview.assertTestCountMatches(
            total = 2,
            skipped = 1
        )
    }

    @Test
    fun iosLegacyJUnitResultTest() {
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
        Truth.assertThat(resultDirectory.toJUnitXmlFile().exists()).isTrue()

        val outputReport = resultDirectory.toOutputReportFile().json().asOutputReport()

        Truth.assertThat(outputReport.error).isEmpty()
        Truth.assertThat(outputReport.cost).isNotNull()

        Truth.assertThat(outputReport.testResults.count()).isEqualTo(1)
        Truth.assertThat(outputReport.weblinks.count()).isEqualTo(1)

        val testSuiteOverview = outputReport.firstTestSuiteOverview

        testSuiteOverview.assertTestCountMatches(
            total = 17,
            skipped = 0
        )
    }
}
