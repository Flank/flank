package integration

import FlankCommand
import com.google.common.truth.Truth
import org.junit.Test
import run

class GameloopIT {

    private val name = this::class.java.simpleName

    @Test
    fun androidGameloop() {
        val name = "$name-android"
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/flank_android_gameloop_success.yml",
            params = androidRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 0)

        val resOutput = result.output.removeUnicode()
        Truth.assertThat(resOutput).containsMatch(findInCompare(name))
        assertContainsOutcomeSummary(resOutput) {
            success = 1
        }
    }
}
