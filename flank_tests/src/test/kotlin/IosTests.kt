import org.junit.Assert
import org.junit.Test
import utils.getPropertyAsList

class IosTests {
    private val flankPath = System.getProperty("flank-path").orEmpty()
    private val ymlPath = System.getProperty("yml-path").orEmpty()
    private val iOSRunParams =
        listOf("firebase", "test", "ios", "run") + getPropertyAsList("run-params")
    private val workingDirectory = System.getProperty("working-directory") ?: "./"

    private val iOSOutputPattern =
        "IosArgs.*?" +
                "gcloud:.*?" +
                "flank:.*?" +
                "RunTests.*?" +
                "Matrices webLink.*?" +
                "matrix-.*?" +
                "FetchArtifacts.*?" +
                "Updating matrix file.*?" +
                "CostReport.*?" +
                "MatrixResultsReport.*?" +
                "test cases passed.*?" +
                "Uploading JUnitReport.xml ."

    @Test
    fun `should run flank with ios tests and match output`() {
        val actual =
            FlankCommand(flankPath, ymlPath, iOSRunParams).run(workingDirectory)
        val expectedExitCode = 0
        Assert.assertEquals(
            "Expected exit code is: $expectedExitCode but actual: ${actual.exitCode}",
            expectedExitCode,
            actual.exitCode
        )
        val expectedOutput = iOSOutputPattern.toRegex(setOf(RegexOption.DOT_MATCHES_ALL))
        Assert.assertTrue(
            "Output don't match pattern, actual output: ${actual.output}",
            expectedOutput.find(actual.output)?.value.orEmpty().isNotBlank()
        )
    }
}
