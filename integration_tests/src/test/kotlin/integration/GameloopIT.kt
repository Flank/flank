package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import flank.common.isLinux
import flank.common.isMacOS
import flank.common.isWindows
import org.junit.Assume.assumeFalse
import org.junit.Test
import run
import utils.CONFIGS_PATH
import utils.FLANK_JAR_PATH
import utils.androidRunCommands
import utils.asOutputReport
import utils.assertCostMatches
import utils.assertExitCode
import utils.findTestDirectoryFromOutput
import utils.iosRunCommands
import utils.json
import utils.removeUnicode
import utils.toOutputReportFile
import java.math.BigDecimal

class GameloopIT {

    private val name = this::class.java.simpleName

    @Test
    fun androidGameloop() {
        val name = "$name-android"
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/flank_android_gameloop_success.yml",
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

        val testAxis = outputReport.testResults.values.first().first()
        assertThat(testAxis.outcome).isEqualTo("success")
    }

    @Test
    fun iosGameloop() {
        assumeFalse(isWindows)
        assumeFalse(isMacOS)
        assumeFalse(isLinux)
        val name = "$name-ios"
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/flank_ios_gameloop_success.yml",
            params = iosRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 0)

        val resOutput = result.output.removeUnicode()

        val outputReport = resOutput.findTestDirectoryFromOutput().toOutputReportFile().json().asOutputReport()

        assertThat(outputReport.error).isEmpty()
        assertThat(outputReport.cost).isNotNull()

        assertThat(outputReport.cost?.physical).isEqualToIgnoringScale(BigDecimal.ZERO)
        assertThat(outputReport.cost?.virtual).isEqualToIgnoringScale("0.02")
        assertThat(outputReport.cost?.total).isEqualTo(outputReport.cost?.virtual)

        assertThat(outputReport.testResults.count()).isEqualTo(1)
        assertThat(outputReport.weblinks.count()).isEqualTo(1)

        val testAxis = outputReport.testResults.values.first().first()
        assertThat(testAxis.outcome).isEqualTo("success")
    }
}
