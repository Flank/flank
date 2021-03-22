package integration

import FlankCommand
import com.google.common.truth.Truth
import org.junit.Test
import run
import utils.containsAll
import utils.loadAndroidDumpShards
import utils.loadIosDumpShards
import java.io.File

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

        val matrix = File("android_shards.json").loadAndroidDumpShards()

        Truth.assertThat(matrix.shards.count()).isEqualTo(2)

        Truth.assertThat(matrix.shards.values.flatten()).containsAll(
            "class com.example.test_app.parametrized.EspressoParametrizedClassParameterizedNamed",
            "class com.example.test_app.parametrized.EspressoParametrizedClassTestParameterized",
            "class com.example.test_app.ParameterizedTest",
            "class com.example.test_app.parametrized.EspressoParametrizedMethodTestJUnitParamsRunner",
        )

        Truth.assertThat(matrix.junitIgnored.count()).isEqualTo(4)
        Truth.assertThat(matrix.junitIgnored).containsNoDuplicates()

        Truth.assertThat(matrix.junitIgnored)
            .containsExactly(
                "class com.example.test_app.InstrumentedTest#ignoredTestWitSuppress",
                "class com.example.test_app.InstrumentedTest#ignoredTestWithIgnore",
                "class com.example.test_app.bar.BarInstrumentedTest#ignoredTestBar",
                "class com.example.test_app.foo.FooInstrumentedTest#ignoredTestFoo"
            )
    }

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
        Truth.assertThat(resOutput).containsMatch(findInCompare(name))
        assertNoOutcomeSummary(resOutput)

        val shards = File("ios_shards.json").loadIosDumpShards()

        Truth.assertThat(shards.count()).isEqualTo(2)

        shards.first().let { firstShard ->
            Truth.assertThat(firstShard.count()).isEqualTo(8)
            Truth.assertThat(firstShard)
                .contains("EarlGreyExampleSwiftTests/testWithCustomFailureHandler")
        }
    }
}
