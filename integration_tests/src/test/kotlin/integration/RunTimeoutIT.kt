package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import run
import utils.asOutputReport
import utils.findTestDirectoryFromOutput
import utils.json
import utils.toOutputReportFile

class RunTimeoutIT {
    private val name = this::class.java.simpleName
    @Test
    fun `cancel test run on timeout`() {
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/flank_android_run_timeout.yml",
            params = androidRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 1)

        val resOutput = result.output.removeUnicode()
        val outputReport = resOutput.findTestDirectoryFromOutput().toOutputReportFile().json().asOutputReport()
        assertThat(outputReport.cost).isNull()
        assertThat(outputReport.testResults).isEmpty()
        assertThat(outputReport.weblinks).isEmpty()
    }
}
