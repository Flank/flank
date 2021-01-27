package flank.scripts.ops.dependencies

import flank.common.isWindows
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeFalse
import org.junit.Test
import java.io.File

class UpdateDependenciesTest {
    private val testReport = File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/report.json")
    private val testDependencies =
        File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/TestDependencies")
    private val testVersions = File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/TestVersions")

    @Test
    fun `should update dependencies`() {
        // assume
        assumeFalse(isWindows)

        // given
        val copyOfTestVersions =
            testVersions.copyTo(
                File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/VersionsAfterUpdateDependencies")
            )
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
