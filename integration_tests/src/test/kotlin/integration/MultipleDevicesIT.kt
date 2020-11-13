package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import run
import org.junit.Test

class MultipleDevicesIT {
    private val name = this::class.java.simpleName

    @Test
    fun `run tests on multiple devices - android`() {
        val name = "$name-android"
        val result = FlankCommand(
            flankPath = "../test_runner/build/libs/flank.jar",
            ymlPath = "./src/test/resources/cases/flank_android_multiple_devices.yml",
            params = androidRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 10)

        val resOutput = result.output.removeUnicode()
        assertThat(resOutput).containsMatch(findInCompare(name))
        assertContainsOutcomeSummary(resOutput) {
            success = 6
            failure = 3
        }
    }
}
