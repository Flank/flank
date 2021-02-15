package integration

import FlankCommand
import com.google.common.truth.Truth
import flank.common.isLinux
import flank.common.isMacOS
import flank.common.isWindows
import org.junit.Assume.assumeFalse
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

    @Test
    fun iosGameloop() {
        assumeFalse(isWindows)
        assumeFalse(isMacOS)
        assumeFalse(isLinux)
        print("Ye old print")
        val name = "$name-ios"
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/flank_ios_gameloop_success.yml",
            params = iosRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 0)

        val resOutput = result.output.removeUnicode()
        Truth.assertThat(resOutput).containsMatch(findInCompare(name))
    }
}
