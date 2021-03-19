package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import flank.common.isWindows
import org.junit.Assume.assumeFalse
import org.junit.Test
import run
import utils.asOutputReport
import utils.findTestDirectoryFromOutput
import utils.json
import utils.toOutputReportFile

class AllTestFilteredIT {
    private val name = this::class.java.simpleName

    @Test
    fun `filter all tests - android`() {
        val name = "$name-android"
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/all_test_filtered_android.yml",
            params = androidRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 1)

        val resOutput = result.output.removeUnicode()

        val outputReport = resOutput.findTestDirectoryFromOutput().toOutputReportFile().json().asOutputReport()

        assertNoOutcomeSummary(resOutput)

        assertThat(outputReport.error).contains("There are no tests to run.")
        assertThat(outputReport.cost).isNull()
        assertThat(outputReport.testResults).isEmpty()
        assertThat(outputReport.weblinks).isEmpty()
    }

    @Test
    fun `filter all tests - ios`() {
        assumeFalse(isWindows)
        val name = "$name-ios"
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/all_test_filtered_ios.yml",
            params = iosRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 1)

        val resOutput = result.output.removeUnicode()

        assertNoOutcomeSummary(resOutput)

        val outputReport = resOutput.findTestDirectoryFromOutput().toOutputReportFile().json().asOutputReport()

        assertNoOutcomeSummary(resOutput)

        assertThat(outputReport.error).contains("Empty shards. Cannot match any method to [nonExisting/Class]")
        assertThat(outputReport.cost).isNull()
        assertThat(outputReport.testResults).isEmpty()
        assertThat(outputReport.weblinks).isEmpty()
    }
}
