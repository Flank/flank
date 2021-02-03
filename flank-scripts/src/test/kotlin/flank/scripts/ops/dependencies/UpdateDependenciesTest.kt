package flank.scripts.ops.dependencies

import flank.common.isWindows
import flank.scripts.ops.dependencies.testfiles.testFilesPath
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeFalse
import org.junit.Test
import java.io.File

class UpdateDependenciesTest {
    private val testReport = File("${testFilesPath}report.json")
    private val testDependencies =
        File("${testFilesPath}TestDependencies")
    private val testVersions = File("${testFilesPath}TestVersions")

    @Test
    fun `should update dependencies`() {
        // assume
        assumeFalse(isWindows)

        // given
        val copyOfTestVersions =
            testVersions.copyTo(
                File("${testFilesPath}VersionsAfterUpdateDependencies")
            )
        val expectedVersions =
            File("${testFilesPath}ExpectedVersionAfterUpdateDependencies")

        // when
        testReport.updateDependencies(testDependencies, copyOfTestVersions)

        // then
        assertEquals(copyOfTestVersions.readText(), expectedVersions.readText())

        // clean up
        copyOfTestVersions.delete()
    }
}
