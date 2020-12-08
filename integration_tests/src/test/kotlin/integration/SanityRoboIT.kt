package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import run
import utils.assertCountOfFailedTests
import utils.assertCountOfSuccessTests
import utils.assertTestResultContainsWebLinks
import utils.findTestDirectoryFromOutput
import utils.loadAsTestSuite
import utils.toJUnitXmlFile

class SanityRoboIT {
    private val name = this::class.java.simpleName

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
        assertThat(resOutput).containsMatch(findInCompare(name))
        assertContainsOutcomeSummary(resOutput) {
            success = 1
        }

        resOutput.findTestDirectoryFromOutput().toJUnitXmlFile().loadAsTestSuite().run {
            assertTestResultContainsWebLinks()
            assertCountOfFailedTests(0)
            assertCountOfSuccessTests(1)
        }
    }
}
