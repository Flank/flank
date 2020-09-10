import org.junit.Assert
import org.junit.Test
import utils.toStringMap

class IntegrationTests {
    @Test
    fun shouldMatchSuccessAndroidExitCodeAndSuccessPattern() {
        val testParameters = System.getProperties().toStringMap().toAndroidTestParameters()
        val actual =
            FlankCommand(testParameters.flankPath, testParameters.ymlPath, testParameters.runParams).run(testParameters.workingDirectory)
        Assert.assertEquals(
            "Expected exit code is: ${testParameters.expectedOutputCode} but actual: ${actual.exitCode}",
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
    fun shouldMatchIosSuccessExitCodeAndSuccessPattern() {
        val testParameters = System.getProperties().toStringMap().toIosParameters()
        val actual =
            FlankCommand(testParameters.flankPath, testParameters.ymlPath, testParameters.runParams).run(testParameters.workingDirectory)
        Assert.assertEquals(
            "Expected exit code is: ${testParameters.expectedOutputCode} but actual: ${actual.exitCode}",
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
