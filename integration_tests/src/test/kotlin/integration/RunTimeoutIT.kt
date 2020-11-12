package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import run

class RunTimeoutIT {

    @Test
    fun `cancel test run on timeout`() {
        val name = this::class.java.simpleName
        val result = FlankCommand(
            flankPath = "../test_runner/build/libs/flank.jar",
            ymlPath = "./src/test/resources/cases/flank_android_run_timeout.yml",
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
}
