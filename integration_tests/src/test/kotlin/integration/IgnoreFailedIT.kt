package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import run

class IgnoreFailedIT {
    private val name = this::class.java.simpleName
    @Test
    fun `return with exit code 0 for failed tests`() {
        val result = FlankCommand(
            flankPath = "../test_runner/build/libs/flank.jar",
            ymlPath = "./src/test/resources/cases/flank_android_ignore_failed.yml",
            params = androidRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 0)

        val resOutput = result.output.removeUnicode()
        assertThat(resOutput).containsMatch(findInCompare(name))
        assertContainsOutcomeSummary(resOutput) {
            failure = 1
        }
    }
}
