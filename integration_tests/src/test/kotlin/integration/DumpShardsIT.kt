package integration

import FlankCommand
import com.google.common.truth.Truth
import flank.common.isWindows
import org.junit.Assume
import org.junit.Test
import run

class DumpShardsIT {
    private val name = this::class.java.simpleName

    @Test
    fun `dump shards - android`() {
        val name = "$name-android"
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/dump_shards_android.yml",
            params = androidRunCommands + "--dump-shards"
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 0)

        val resOutput = result.output.removeUnicode()
        Truth.assertThat(resOutput).containsMatch(findInCompare(name))
        assertNoOutcomeSummary(resOutput)
    }

    @Test
    fun `dump shards - ios`() {
        Assume.assumeFalse(isWindows)
        val name = "$name-ios"
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/dump_shards_ios.yml",
            params = iosRunCommands + "--dump-shards"
        ).run(
            workingDirectory = "./",
            testSuite = name
        )

        assertExitCode(result, 0)

        val resOutput = result.output.removeUnicode()
        Truth.assertThat(resOutput).containsMatch(findInCompare(name))
        assertNoOutcomeSummary(resOutput)
    }
}
