package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import run
import utils.assertCountOfFailedTests
import utils.assertTestResultContainsWebLinks
import utils.findTestDirectoryFromOutput
import utils.loadAsTestSuite
import utils.toJUnitXmlFile

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
        assertThat(resOutput).containsMatch(findInCompare(name))
        assertContainsOutcomeSummary(resOutput) {
            success = 1
        }
        resOutput.findTestDirectoryFromOutput().toJUnitXmlFile().loadAsTestSuite().run {
            assertTestResultContainsWebLinks()
            assertCountOfFailedTests(0)
        }
    }
}
