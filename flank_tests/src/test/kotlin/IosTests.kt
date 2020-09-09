import org.junit.Assert
import org.junit.Test

class IosTests {
    private val flankPath = System.getProperty("flank-path").orEmpty()
    private val ymlPath = System.getProperty("yml-path").orEmpty()
    private val iOSRunParams = System.getProperty("run-params").orEmpty().split(",")
    private val workingDirectory = System.getProperty("working-directory")

    private val iOSOutputPattern =
        """IosArgs.*?gcloud:.*?flank:.*?RunTests.*?Matrices webLink.*?matrix-.*?FetchArtifacts.*?Updating matrix file.*?CostReport.*?MatrixResultsReport.*?test cases passed.*?Uploading JUnitReport.xml ."""

    @Test
    fun `should run flank with android tests`() {
        val actual =
            FlankCommand(flankPath, ymlPath, iOSRunParams).run(workingDirectory)
        val expectedExitCode = 0
        Assert.assertEquals(expectedExitCode, actual.exitCode)
        val expectedOutput = iOSOutputPattern.toRegex(setOf(RegexOption.DOT_MATCHES_ALL))
        Assert.assertTrue("Output don't match pattern", expectedOutput.find(actual.output)?.value.orEmpty().isNotBlank())
    }
}
