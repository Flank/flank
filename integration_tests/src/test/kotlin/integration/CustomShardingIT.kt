package integration

import FlankCommand
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.common.truth.Truth.assertThat
import flank.common.isWindows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import run
import utils.AndroidTestShards
import utils.CONFIGS_PATH
import utils.FLANK_JAR_PATH
import utils.androidRunCommands
import utils.asOutputReport
import utils.assertContainsUploads
import utils.assertCostMatches
import utils.assertExitCode
import utils.assertTestFail
import utils.assertTestPass
import utils.assertTestResultContainsWebLinks
import utils.findTestDirectoryFromOutput
import utils.json
import utils.loadAsTestSuite
import utils.multipleFailedTests
import utils.multipleSuccessfulTests
import utils.removeUnicode
import utils.toJUnitXmlFile
import utils.toOutputReportFile
import java.nio.file.Paths

class CustomShardingIT {
    private val name = this::class.java.simpleName

    private val jsonMapper by lazy { JsonMapper().registerModule(KotlinModule()) }

    @get:Rule
    val root = TemporaryFolder()

    @Test
    fun `flank custom sharding -- android`() {

        val templateConfigPath =
            "$CONFIGS_PATH/flank_android_custom_sharding.yml"

        val customShardingPath = root.newFile("custom_sharding.json").also {
            it.writeText(jsonMapper.writeValueAsString(customSharding))
        }.absolutePath

        val config = root.newFile("flank.yml").also {
            it.writeText(
                Paths.get(templateConfigPath)
                    .toFile()
                    .readText()
                    .replace("{{PLACEHOLDER}}", customShardingPath)
            )
        }.absolutePath

        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = config,
            params = androidRunCommands
        ).run("./", name)

        assertExitCode(result, 10)

        val resOutput = result.output.removeUnicode()

        assertContainsUploads(
            resOutput,
            "app-multiple-success-debug-androidTest.apk",
            "app-multiple-error-debug-androidTest.apk",
            "MainActivity_robo_script.json"
        )

        resOutput.findTestDirectoryFromOutput().toJUnitXmlFile().loadAsTestSuite().run {
            assertTestResultContainsWebLinks()
            assertTestPass(multipleSuccessfulTests)
            assertTestFail(multipleFailedTests)

            assertThat(testSuites.size).isEqualTo(9)
            assertThat(testSuites.filter { it.name == "junit-ignored" }.size).isEqualTo(1)
        }

        val outputReport = resOutput.findTestDirectoryFromOutput().toOutputReportFile().json().asOutputReport()

        assertThat(outputReport.error).isEmpty()
        assertThat(outputReport.cost).isNotNull()

        outputReport.assertCostMatches()

        assertThat(outputReport.testResults.count()).isEqualTo(4)
        assertThat(outputReport.weblinks.count()).isEqualTo(4)

        val testsResults = outputReport.testResults
            .map { it.value }
            .map { it.testAxises }
            .flatten()

        assertThat(testsResults.sumBy { it.testSuiteOverview.failures }).isEqualTo(5)
        assertThat(testsResults.sumBy { it.testSuiteOverview.total }).isEqualTo(41)
    }
}

val customSharding =
    mapOf(
        "matrix-0" to AndroidTestShards(
            app = "../test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk",
            test = "gs://flank-open-source.appspot.com/integration/app-single-success-debug-androidTest.apk",
            shards = mapOf(
                "shard-0" to listOf(
                    "class com.example.test_app.InstrumentedTest#test"
                )
            ),
            junitIgnored = listOf(
                "class com.example.test_app.InstrumentedTest#ignoredTestWithIgnore",
                "class com.example.test_app.InstrumentedTest#ignoredTestWithSuppress"
            )
        ),
        "matrix-1" to AndroidTestShards(
            app = "../test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk",
            test = "../test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-error-debug-androidTest.apk",
            shards = mapOf(
                "shard-0" to listOf(
                    "class com.example.test_app.InstrumentedTest#test1",
                    "class com.example.test_app.InstrumentedTest#test2"
                ),
                "shard-1" to listOf(
                    "class com.example.test_app.ParameterizedTest",
                    "class com.example.test_app.parametrized.EspressoParametrizedMethodTestJUnitParamsRunner",
                    "class com.example.test_app.InstrumentedTest#test0"
                ),
                "shard-2" to listOf(
                    "class com.example.test_app.parametrized.EspressoParametrizedClassParameterizedNamed",
                    "class com.example.test_app.parametrized.EspressoParametrizedClassTestParameterized"
                ),
                "shard-3" to listOf(
                    "class com.example.test_app.bar.BarInstrumentedTest#testBar",
                    "class com.example.test_app.foo.FooInstrumentedTest#testFoo"
                )
            ),
            junitIgnored = listOf(
                "class com.example.test_app.InstrumentedTest#ignoredTestWitSuppress",
                "class com.example.test_app.InstrumentedTest#ignoredTestWithIgnore",
                "class com.example.test_app.bar.BarInstrumentedTest#ignoredTestBar",
                "class com.example.test_app.foo.FooInstrumentedTest#ignoredTestFoo"
            )
        ),
        "matrix-2" to AndroidTestShards(
            app = "../test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk",
            test = "../test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-success-debug-androidTest.apk",
            shards = mapOf(
                "shard-0" to listOf(
                    "class com.example.test_app.InstrumentedTest#test1",
                    "class com.example.test_app.InstrumentedTest#test2"
                ),
                "shard-1" to listOf(
                    "class com.example.test_app.ParameterizedTest",
                    "class com.example.test_app.parametrized.EspressoParametrizedMethodTestJUnitParamsRunner"
                ),
                "shard-2" to listOf(
                    "class com.example.test_app.InstrumentedTest#test0",
                    "class com.example.test_app.bar.BarInstrumentedTest#testBar",
                    "class com.example.test_app.foo.FooInstrumentedTest#testFoo",
                    "class com.example.test_app.parametrized.EspressoParametrizedClassParameterizedNamed",
                    "class com.example.test_app.parametrized.EspressoParametrizedClassTestParameterized"
                )
            ),
            junitIgnored = listOf(
                "class com.example.test_app.InstrumentedTest#ignoredTestWitSuppress",
                "class com.example.test_app.InstrumentedTest#ignoredTestWithIgnore",
                "class com.example.test_app.bar.BarInstrumentedTest#ignoredTestBar",
                "class com.example.test_app.foo.FooInstrumentedTest#ignoredTestFoo"
            )
        )
    ).run {
        // we need to change files paths to make tests happy when started on windows OS
        if (isWindows) mapValues { (_, shards) ->
            shards.copy(
                app = shards.app.replace("/", "\\"),
                test = shards.test.replace("/", "\\")
            )
        }
        else this
    }
