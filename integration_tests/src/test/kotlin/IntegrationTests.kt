import org.junit.Assert
import org.junit.Test
import utils.toStringMap
import java.io.File

class IntegrationTests {
    @Test
    fun shouldMatchAndroidSuccessExitCodeAndPattern() {
        val testParameters = System.getProperties().toStringMap().toAndroidTestParameters()
        val actual =
            FlankCommand(testParameters.flankPath, testParameters.ymlPath, testParameters.runParams).run(testParameters.workingDirectory)
        val file = File("../test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk")
        Assert.assertTrue("File: ${file.absolutePath} not exists", file.exists())

        Assert.assertEquals(
            "Expected exit code is: ${testParameters.expectedOutputCode} but actual: ${actual.exitCode}, output:\n${actual.output}",
            testParameters.expectedOutputCode,
            actual.exitCode
        )
        val expectedOutput = testParameters.outputPattern.toRegex(
            setOf(RegexOption.DOT_MATCHES_ALL)
        )
        Assert.assertTrue(
            "Output don't match pattern, actual output: ${actual.output}",
            expectedOutput.find(actual.output)?.value.orEmpty().isNotBlank()
        )
    }

    @Test
    fun shouldMatchIosSuccessExitCodeAndPattern() {
        val testParameters = System.getProperties().toStringMap().toIosParameters()
        val actual =
            FlankCommand(testParameters.flankPath, testParameters.ymlPath, testParameters.runParams).run(testParameters.workingDirectory)

        val file = File("../test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk")
        Assert.assertTrue("File: ${file.absolutePath} not exists", file.exists())

        Assert.assertEquals(
            "Expected exit code is: ${testParameters.expectedOutputCode} but actual: ${actual.exitCode}, output:\n${actual.output}",
            testParameters.expectedOutputCode,
            actual.exitCode
        )
        val expectedOutput = testParameters.outputPattern.toRegex(
            setOf(RegexOption.DOT_MATCHES_ALL)
        )
        Assert.assertTrue(
            "Output don't match pattern, actual output: ${actual.output}",
            expectedOutput.find(actual.output)?.value.orEmpty().isNotBlank()
        )
    }
}
