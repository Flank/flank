package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import flank.common.OUTPUT_ARGS
import flank.common.OUTPUT_COST
import flank.common.OUTPUT_ERROR
import flank.common.OUTPUT_TEST_RESULTS
import flank.common.OUTPUT_WEBLINKS
import org.junit.Test
import run
import utils.assertCountOfFailedTests
import utils.assertTestPass
import utils.assertTestResultContainsWebLinks
import utils.findTestDirectoryFromOutput
import utils.loadAsTestSuite
import utils.loadJsonResults
import utils.toJUnitXmlFile
import utils.toOutputReportFile

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
        val json = resOutput.findTestDirectoryFromOutput().toOutputReportFile().loadJsonResults()

        println(json)
        assertThat(json).containsKey(OUTPUT_ARGS)
        assertThat(json).containsKey(OUTPUT_COST)
        assertThat(json).containsKey(OUTPUT_TEST_RESULTS)

        assertThat(json).containsKey(OUTPUT_WEBLINKS)
        assertThat(json).doesNotContainKey(OUTPUT_ERROR)

        assertThat(resOutput).containsMatch(findInCompare(name))
        assertContainsOutcomeSummary(resOutput) {
            success = 1
        }
        resOutput.findTestDirectoryFromOutput().toJUnitXmlFile().loadAsTestSuite().run {
            assertTestResultContainsWebLinks()
            assertCountOfFailedTests(0)
            assertTestPass(listOf("test2"))
        }
    }
}
