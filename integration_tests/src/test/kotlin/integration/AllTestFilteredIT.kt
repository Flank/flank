package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import run
import org.junit.Test

class AllTestFilteredIT {

    @Test
    fun `filter all tests - android`() {
        val name = this::class.java.simpleName + "-android"
        val result = FlankCommand(
            flankPath = "../test_runner/build/libs/flank.jar",
            ymlPath = "./src/test/resources/cases/all_test_filtered_android.yml",
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
        val name = this::class.java.simpleName + "-ios"
        val result = FlankCommand(
            flankPath = "../test_runner/build/libs/flank.jar",
            ymlPath = "./src/test/resources/cases/all_test_filtered_ios.yml",
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
