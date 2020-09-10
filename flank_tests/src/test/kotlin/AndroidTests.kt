import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import utils.getPropertyAsList

class AndroidTests {
    private val flankPath = System.getProperty("flank-path").orEmpty()
    private val ymlPath = System.getProperty("yml-path").orEmpty()
    private val androidRunParams =
        listOf("firebase", "test", "android", "run") + getPropertyAsList("run-params")
    private val workingDirectory = System.getProperty("working-directory").orEmpty()

    private val androidOutputPattern =
        "AndroidArgs.*?" +
                "gcloud:.*?" +
                "flank:.*?" +
                "RunTests.*?" +
                "Matrices webLink.*?" +
                "matrix-.*?" +
                "FetchArtifacts.*?" +
                "Updating matrix file.*?" +
                "CostReport.*?MatrixResultsReport.*?" +
                "1 test cases passed, 1 skipped.*?" +
                "Uploading JUnitReport.xml ."

    @Test
    fun `should run flank with android tests and match output`() {
        val actual =
            FlankCommand(flankPath, ymlPath, androidRunParams).run(workingDirectory)
        val expectedExitCode = 0
        assertEquals(
            "Expected exit code is: $expectedExitCode but actual: ${actual.exitCode}",
            expectedExitCode,
            actual.exitCode
        )
        val expectedOutput = androidOutputPattern.toRegex(
            setOf(RegexOption.DOT_MATCHES_ALL)
        )
        assertTrue(
            "Output don't match pattern, actual output: ${actual.output}",
            expectedOutput.find(actual.output)?.value.orEmpty().isNotBlank()
        )
    }
}
