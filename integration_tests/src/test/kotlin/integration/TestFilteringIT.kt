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

class TestFilteringIT {
    private val name = this::class.java.simpleName

    @Category(AndroidTest::class)
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

        outputReport.assertCostMatches()

        outputReport.firstTestSuiteOverview.assertTestCountMatches(total = 1)
    }
}
