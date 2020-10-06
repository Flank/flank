package flank.scripts.dependencies.update

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class UpdateGradleTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private val testReport = File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/report.json")
    private val testGradleVersionFile =
        File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/test_gradle-wrapper.properties.test")

    @Test
    fun `Should update gradle`() {
        // given
        val expectedVersions =
            File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/expected_gradle-wrapper.properties.test")
        val copyOfTestVersions = tempFolder.newFile("gradle-wrapper.properties").apply {
            writeText(testGradleVersionFile.readText())
        }

        // when
        testReport.updateGradle(tempFolder.root.absolutePath)

        // then
        assertEquals(copyOfTestVersions.readText(), expectedVersions.readText())
    }

    @Test
    fun `Should update RC version gradle`() {
        val rcReportText = testReport.readText().replace("\r\n", "\n").replace(
            """
    |        "releaseCandidate": {
    |            "version": "6.7-rc-1",
    |            "reason": "",
    |            "isUpdateAvailable": false,
    |            "isFailure": false
    |        }
        """.trimMargin(), """
    |        "releaseCandidate": {
    |            "version": "6.7-rc-1",
    |            "reason": "",
    |            "isUpdateAvailable": true,
    |            "isFailure": false
    |        }
        """.trimMargin()
        )

        val rcReport = tempFolder.newFile("rcReport.json").apply { writeText(rcReportText) }

        // given
        val expectedVersions =
            File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/expected_gradle-wrapper.properties_RC.test")
        val copyOfTestVersions = tempFolder.newFile("gradle-wrapper.properties").apply {
            writeText(testGradleVersionFile.readText())
        }

        // when
        rcReport.updateGradle(tempFolder.root.absolutePath)

        // then
        assertEquals(copyOfTestVersions.readText(), expectedVersions.readText())
    }
}
