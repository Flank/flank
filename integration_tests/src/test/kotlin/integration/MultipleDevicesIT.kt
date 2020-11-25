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
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/flank_android_multiple_devices.yml",
            params = androidRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 10)

        val resOutput = result.output.removeUnicode()
        assertThat(resOutput).containsMatch(findInCompare(name))
        assertContainsUploads(resOutput,
            "Uploading app-multiple-success-debug-androidTest.apk",
            "Uploading app-multiple-error-debug-androidTest.apk",
            "Uploading performanceMetrics.json"
        )
        assertContainsOutcomeSummary(resOutput) {
            success = 6
            failure = 3
        }
    }
}
