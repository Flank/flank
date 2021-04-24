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
import utils.assertTestCountMatches
import utils.findTestDirectoryFromOutput
import utils.firstTestSuiteOverview
import utils.json
import utils.removeUnicode
import utils.toOutputReportFile

class IgnoreFailedIT {
    private val name = this::class.java.simpleName

    @Category(AndroidTest::class)
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

        outputReport.assertCostMatches()

        assertThat(outputReport.testResults.count()).isEqualTo(1)
        assertThat(outputReport.weblinks.count()).isEqualTo(1)

        val testSuiteOverview = outputReport.firstTestSuiteOverview

        testSuiteOverview.assertTestCountMatches(
            total = 1,
            failures = 1
        )
    }
}
