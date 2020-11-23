package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import run

class MultipleApksIT {
    private val name = this::class.java.simpleName

    @Test
    fun `flank full option run`() {
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/flank_android_multiple_apk.yml",
            params = androidRunCommands
        ).run("./", name)

        assertExitCode(result, 10)

        val resOutput = result.output.removeUnicode()
        assertThat(resOutput).containsMatch(findInCompare(name))
        listOf(
            "Uploading app-multiple-success-debug-androidTest.apk",
            "Uploading app-multiple-error-debug-androidTest.apk",
            "Uploading MainActivity_robo_script.json"
        ).forEach {
            assertThat(resOutput).contains(it)
        }

        assertContainsOutcomeSummary(resOutput) {
            success = 3
            failure = 1
        }
    }
}
