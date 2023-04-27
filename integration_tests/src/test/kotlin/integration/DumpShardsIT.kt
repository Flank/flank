package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import integration.config.AndroidTest
import integration.config.IosTest
import org.junit.Test
import org.junit.experimental.categories.Category
import run
import utils.CONFIGS_PATH
import utils.FLANK_JAR_PATH
import utils.androidRunCommands
import utils.assertExitCode
import utils.assertNoOutcomeSummary
import utils.containsAll
import utils.findInCompare
import utils.iosRunCommands
import utils.loadAndroidDumpShards
import utils.loadIosDumpShards
import utils.removeUnicode
import java.io.File

class DumpShardsIT {
    private val name = this::class.java.simpleName

    @Category(AndroidTest::class)
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
        assertThat(resOutput).containsMatch(findInCompare(name))
        assertNoOutcomeSummary(resOutput)

        val matrix = File("android_shards.json").loadAndroidDumpShards()

        assertThat(matrix.shards.count()).isEqualTo(2)

        assertThat(matrix.shards.values.flatten()).containsAll(
            "class com.example.test_app.parametrized.EspressoParametrizedClassParameterizedNamed",
            "class com.example.test_app.parametrized.EspressoParametrizedClassTestParameterized",
            "class com.example.test_app.ParameterizedTest",
            "class com.example.test_app.parametrized.EspressoParametrizedMethodTestJUnitParamsRunner",
        )

        assertThat(matrix.junitIgnored.count()).isEqualTo(4)
        assertThat(matrix.junitIgnored).containsNoDuplicates()

        assertThat(matrix.junitIgnored)
            .containsExactly(
                "class com.example.test_app.InstrumentedTest#ignoredTestWitSuppress",
                "class com.example.test_app.InstrumentedTest#ignoredTestWithIgnore",
                "class com.example.test_app.bar.BarInstrumentedTest#ignoredTestBar",
                "class com.example.test_app.foo.FooInstrumentedTest#ignoredTestFoo"
            )
    }

    @Category(IosTest::class)
    @Test
    fun `dump shards - ios`() {
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
        assertThat(resOutput).containsMatch(findInCompare(name))
        assertNoOutcomeSummary(resOutput)

        val shards = File("ios_shards.json").loadIosDumpShards()

        assertThat(shards.count()).isEqualTo(2)

        shards.first().values.flatten().let { firstShard ->
            assertThat(firstShard.count()).isEqualTo(2)
            assertThat(firstShard).contains("FlankExampleTests/test2")
        }
    }
}
