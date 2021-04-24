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

class SanityRoboIT {

    @Category(AndroidTest::class)
    @Test
    fun `sanity robo`() {
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/sanity_robo.yml",
            params = androidRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = this::class.java.simpleName
        )

        assertExitCode(result, 0)

        val resOutput = result.output.removeUnicode()
        val outputReport = resOutput.findTestDirectoryFromOutput().toOutputReportFile().json().asOutputReport()

        assertThat(outputReport.cost).isNotNull()
        outputReport.assertCostMatches()

        assertThat(outputReport.testResults).isNotEmpty()
        println(outputReport.testResults)
        assertThat(outputReport.weblinks).isNotEmpty()
    }
}
