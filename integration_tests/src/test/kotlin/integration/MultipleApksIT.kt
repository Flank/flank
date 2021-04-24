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
import utils.assertContainsUploads
import utils.assertCostMatches
import utils.assertExitCode
import utils.assertTestFail
import utils.assertTestPass
import utils.assertTestResultContainsWebLinks
import utils.findTestDirectoryFromOutput
import utils.json
import utils.loadAsTestSuite
import utils.multipleFailedTests
import utils.multipleSuccessfulTests
import utils.removeUnicode
import utils.toJUnitXmlFile
import utils.toOutputReportFile

class MultipleApksIT {
    private val name = this::class.java.simpleName

    @Category(AndroidTest::class)
    @Test
    fun `flank full option run`() {
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/flank_android_multiple_apk.yml",
            params = androidRunCommands
        ).run("./", name)

        assertExitCode(result, 10)

        val resOutput = result.output.removeUnicode()

        assertContainsUploads(
            resOutput,
            "app-multiple-success-debug-androidTest.apk",
            "app-multiple-error-debug-androidTest.apk",
            "MainActivity_robo_script.json"
        )

        resOutput.findTestDirectoryFromOutput().toJUnitXmlFile().loadAsTestSuite().run {
            assertTestResultContainsWebLinks()
            assertTestPass(multipleSuccessfulTests)
            assertTestFail(multipleFailedTests)
        }

        val outputReport = resOutput.findTestDirectoryFromOutput().toOutputReportFile().json().asOutputReport()

        assertThat(outputReport.error).isEmpty()
        assertThat(outputReport.cost).isNotNull()

        outputReport.assertCostMatches()

        assertThat(outputReport.testResults.count()).isEqualTo(4)
        assertThat(outputReport.weblinks.count()).isEqualTo(4)

        val testsResults = outputReport.testResults
            .map { it.value }
            .map { it.testAxises }
            .flatten()

        assertThat(testsResults.sumBy { it.testSuiteOverview.failures }).isEqualTo(5)
        assertThat(testsResults.sumBy { it.testSuiteOverview.total }).isEqualTo(41)
    }
}
