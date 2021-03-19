package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import run
import utils.asOutputReport
import utils.findTestDirectoryFromOutput
import utils.json
import utils.toOutputReportFile

class IgnoreFailedIT {
    private val name = this::class.java.simpleName
    @Test
    fun `return with exit code 0 for failed tests`() {
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/flank_android_ignore_failed.yml",
            params = androidRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 0)

        val resOutput = result.output.removeUnicode()
        val outputReport = resOutput.findTestDirectoryFromOutput().toOutputReportFile().json().asOutputReport()
        assertThat(resOutput).containsMatch(findInCompare(name))
        assertContainsOutcomeSummary(resOutput) {
            failure = 1
        }
    }
}
