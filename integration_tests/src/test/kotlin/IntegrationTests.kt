import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import utils.toStringMap

class IntegrationTests {

    @Test
    fun shouldMatchAndroidSuccessExitCodeAndPattern() {
        val testParameters = System.getProperties().toStringMap().toAndroidTestParameters()
        val actual =
            FlankCommand(testParameters.flankPath, testParameters.ymlPath, testParameters.runParams).run(testParameters.workingDirectory)

        assertEquals(
            "Expected exit code is: ${testParameters.expectedOutputCode} but actual: ${actual.exitCode}, output:\n${actual.output}",
            testParameters.expectedOutputCode,
            actual.exitCode
        )
        val expectedOutput = testParameters.outputPattern.toRegex(
            setOf(RegexOption.DOT_MATCHES_ALL)
        )
        assertTrue(
            "Output don't match pattern, actual output: ${actual.output}",
            expectedOutput.find(actual.output)?.value.orEmpty().isNotBlank()
        )
    }

    @Ignore("iOS has only physical devices, whit current configuration flank's project hits quota limit extremely fast")
    @Test
    fun shouldMatchIosSuccessExitCodeAndPattern() {
        val testParameters = System.getProperties().toStringMap().toIosParameters()
        val actual =
            FlankCommand(testParameters.flankPath, testParameters.ymlPath, testParameters.runParams).run(testParameters.workingDirectory)

        assertEquals(
            "Expected exit code is: ${testParameters.expectedOutputCode} but actual: ${actual.exitCode}, output:\n${actual.output}",
            testParameters.expectedOutputCode,
            actual.exitCode
        )
        val expectedOutput = testParameters.outputPattern.toRegex(
            setOf(RegexOption.DOT_MATCHES_ALL)
        )
        assertTrue(
            "Output don't match pattern, actual output: ${actual.output}",
            expectedOutput.find(actual.output)?.value.orEmpty().isNotBlank()
        )
    }
}
