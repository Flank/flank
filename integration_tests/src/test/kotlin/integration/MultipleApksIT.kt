package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import run
import utils.asOutputReport
import utils.assertTestFail
import utils.assertTestPass
import utils.assertTestResultContainsWebLinks
import utils.findTestDirectoryFromOutput
import utils.json
import utils.loadAsTestSuite
import utils.toJUnitXmlFile
import utils.toOutputReportFile
import java.math.BigDecimal

class MultipleApksIT {
    private val name = this::class.java.simpleName

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

        assertContainsOutcomeSummary(resOutput) {
            success = 3
            failure = 1
        }

        resOutput.findTestDirectoryFromOutput().toJUnitXmlFile().loadAsTestSuite().run {
            assertTestResultContainsWebLinks()
            assertTestPass(multipleSuccessfulTests)
            assertTestFail(multipleFailedTests)
        }

        val outputReport = resOutput.findTestDirectoryFromOutput().toOutputReportFile().json().asOutputReport()

        assertThat(outputReport.error).isEmpty()
        assertThat(outputReport.cost).isNotNull()

        assertThat(outputReport.cost?.physical).isEqualToIgnoringScale(BigDecimal.ZERO)
        assertThat(outputReport.cost?.virtual).isEqualToIgnoringScale("0.35")
        assertThat(outputReport.cost?.total).isEqualTo(outputReport.cost?.virtual)

        assertThat(outputReport.testResults.count()).isEqualTo(4)
        assertThat(outputReport.weblinks.count()).isEqualTo(4)

        val testsResults = outputReport.testResults
            .map { it.value }
            .flatten()

        assertThat(testsResults.sumBy { it.testSuiteOverview.failures }).isEqualTo(5)
        assertThat(testsResults.sumBy { it.testSuiteOverview.total }).isEqualTo(41)
    }
}
