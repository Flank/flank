package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import run

class SanityRoboIT {
    private val name = this::class.java.simpleName

    @Test
    fun `sanity robo`() {
        val result = FlankCommand(
            flankPath = "../test_runner/build/libs/flank.jar",
            ymlPath = "./src/test/resources/cases/sanity_robo.yml",
            params = androidRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = this::class.java.simpleName
        )

        assertExitCode(result, 0)

        val resOutput = result.output.removeUnicode()
        assertThat(resOutput).containsMatch(findInCompare(name))
        assertContainsOutcomeSummary(resOutput) {
            success = 1
        }
    }
}
