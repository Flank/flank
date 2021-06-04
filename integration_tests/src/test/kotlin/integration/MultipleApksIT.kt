package integration

import FlankCommand
import com.google.common.truth.Truth.assertThat
import integration.config.AndroidTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.experimental.categories.Category
import run
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
import utils.multipleSuccessfulTests
import utils.removeUnicode
import utils.testResults.TestSuite
import utils.toJUnitXmlFile
import utils.toOutputReportFile

class MultipleApksIT {
    private val name = this::class.java.simpleName

    @Category(AndroidTest::class)
    @Test
    fun `flank full option run`() {
        val result = FlankCommand(
            flankPath = FLANK_JAR_PATH,
            ymlPath = "$CONFIGS_PATH/flank_android_multiple_apk.yml",
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

        val xmlResult = resOutput.findTestDirectoryFromOutput().toJUnitXmlFile().loadAsTestSuite()

        xmlResult.assertTestResultContainsWebLinks()
        xmlResult.assertTestPass(multipleSuccessfulTests)
        xmlResult.assertTestFail(listOf("test2"))

        xmlResult.testSuites.groupBy { it.name }.mapValues { it.value.flatMap(TestSuite::testCases) }.run {
            assertEquals(20, get("NexusLowRes-28-en-portrait")?.size)
            assertEquals(1, get("Pixel2-28-en-portrait")?.size)
            assertEquals("com.example.test_app.InstrumentedTest", get("Pixel2-28-en-portrait")?.get(0)?.classname)
            assertEquals("test2", get("Pixel2-28-en-portrait")?.get(0)?.name)
            assertEquals(1, get("Nexus6P-27-en-portrait")?.size)
            assertEquals("com.example.test_app.InstrumentedTest", get("Nexus6P-27-en-portrait")?.get(0)?.classname)
            assertEquals("test", get("Nexus6P-27-en-portrait")?.get(0)?.name)
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

        assertThat(testsResults.sumOf { it.suiteOverview.failures }).isEqualTo(1)
        assertThat(testsResults.sumOf { it.suiteOverview.total }).isEqualTo(22)
    }
}
