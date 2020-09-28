package flank.scripts.dependencies.update

import org.junit.Assert.assertEquals
import org.junit.Test
import skipIfWindows
import java.io.File

class UpdateDependenciesTest {
    private val testReport = File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/report.json")
    private val testDependencies =
        File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/TestDependencies")
    private val testVersions = File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/TestVersions")

    @Test
    fun `should update dependencies`() {
        // assume
        skipIfWindows()

        // given
        val copyOfTestVersions =
            testVersions.copyTo(
                File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/VersionsAfterUpdateDependencies"))
        val expectedVersions =
            File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/ExpectedVersionAfterUpdateDependencies")

        // when
        testReport.updateDependencies(testDependencies, copyOfTestVersions)

        // then
        assertEquals(copyOfTestVersions.readText(), expectedVersions.readText())

        // clean up
        copyOfTestVersions.delete()
    }
}
