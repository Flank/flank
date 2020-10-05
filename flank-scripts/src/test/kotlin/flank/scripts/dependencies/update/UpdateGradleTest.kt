package flank.scripts.dependencies.update

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class UpdateGradleTest {
    private val testReport = File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/report.json")
    private val testGradleVersionFile = File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/test_gradle-wrapper.properties.test")

    @Test
    fun `Should update gradle`() {
        // given
        val expectedVersions =
            File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/expected_gradle-wrapper.properties.test")
        val copyOfTestVersions =
            testGradleVersionFile.copyTo(
                File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/gradle-wrapper.properties")).apply { deleteOnExit() }

        // when
        testReport.updateGradle("src/test/kotlin/flank/scripts/dependencies/update/testfiles/")

        // then
        assertEquals(copyOfTestVersions.readText(), expectedVersions.readText())
    }
}
