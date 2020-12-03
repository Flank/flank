package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import org.junit.Assume.assumeFalse
import run
import org.junit.Test

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

        assertExitCode(result, 0)

        val resOutput = result.output.removeUnicode()
        assertThat(resOutput).containsMatch(findInCompare(name))
        assertNoOutcomeSummary(resOutput)
    }
}
