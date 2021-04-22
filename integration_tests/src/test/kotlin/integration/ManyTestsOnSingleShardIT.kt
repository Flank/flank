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
import utils.assertCountOfFailedTests
import utils.assertCountOfSkippedTests
import utils.assertExitCode
import utils.assertTestCountMatches
import utils.findTestDirectoryFromOutput
import utils.firstTestSuiteOverview
import utils.json
import utils.loadAsTestSuite
import utils.removeUnicode
import utils.toJUnitXmlFile
import utils.toOutputReportFile

class ManyTestsOnSingleShardIT {

    private val name = this::class.java.simpleName

    @Category(AndroidTest::class)
    @Test
    fun `return with exit code 0 and has correct output`() {
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/flank_android_many_tests_on_single_shard.yml",
            params = androidRunCommands
        ).run("./", name)
        val expectedTestCount = 60

        assertExitCode(result, 0)

        val resOutput = result.output.removeUnicode()
        val outputDirectory = resOutput.findTestDirectoryFromOutput()

        // validate JUnitReport.xml
        val testSuites = outputDirectory.toJUnitXmlFile().loadAsTestSuite()
        assertThat(testSuites.testSuites.first().testCases.size).isEqualTo(expectedTestCount)
        (0 until expectedTestCount).forEach { testNumber ->
            assertThat(testSuites.testSuites.any { it.name == "test$testNumber" })
            assertThat(testSuites.assertCountOfFailedTests(0))
            assertThat(testSuites.assertCountOfSkippedTests(0))
        }

        // validate general
        val outputReport = outputDirectory.toOutputReportFile().json().asOutputReport()
        outputReport.assertCostMatches()
        assertThat(outputReport.testResults).isNotEmpty()
        assertThat(outputReport.weblinks).isNotEmpty()
        assertThat(outputReport.error).isEmpty()
        outputReport.firstTestSuiteOverview.assertTestCountMatches(total = expectedTestCount)
    }
}
