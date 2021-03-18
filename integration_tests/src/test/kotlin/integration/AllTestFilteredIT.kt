package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import flank.common.OUTPUT_ARGS
import flank.common.OUTPUT_ERROR
import flank.common.isWindows
import org.junit.Assume.assumeFalse
import org.junit.Test
import run
import utils.findTestDirectoryFromOutput
import utils.loadJsonResults
import utils.toOutputReportFile
import java.io.File

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
        assertThat(resOutput).containsMatch(findInCompare(name))
        assertNoOutcomeSummary(resOutput)
        val json = resOutput.findTestDirectoryFromOutput().toOutputReportFile().loadJsonResults()

        assertThat(json).containsKey(OUTPUT_ARGS)
        assertThat(json).containsKey(OUTPUT_ERROR)

        assertThat(json[OUTPUT_ERROR].toString()).contains("There are no tests to run.")
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
        assertThat(resOutput).containsMatch(findInCompare(name))
        assertNoOutcomeSummary(resOutput)
        val json = File(resOutput.findTestDirectoryFromOutput())
    }
}
