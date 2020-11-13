package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import run

class TestFilteringIT {
    private val name = this::class.java.simpleName

    @Test
    fun `run test from only one apk`() {
        val result = FlankCommand(
            flankPath = "../test_runner/build/libs/flank.jar",
            ymlPath = "./src/test/resources/cases/test_filtering_android.yml",
            params = androidRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 0)

        val resOutput = result.output.removeUnicode()
        assertThat(resOutput).containsMatch(findInCompare(name))
        assertContainsOutcomeSummary(resOutput) {
            success = 1
        }
    }
}
