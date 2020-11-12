package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import run

class AndroidFullIT {
    private val name = this::class.java.simpleName

    @Test
    fun `flank full option run`() {
        val result = FlankCommand(
            flankPath = "../test_runner/build/libs/flank.jar",
            ymlPath = "./src/test/resources/cases/flank_android_multiple_apk.yml",
            params = androidRunCommands
        ).run("./", name)


        assertExitCode(result, 10)

        val resOutput = result.output.removeUnicode()
        assertThat(resOutput).containsMatch(findInCompare(name))
        assertContainsOutcomeSummary(resOutput) {
            success = 3
            failure = 1
        }
    }
}
