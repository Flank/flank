package flank.scripts.ops.dependencies

import flank.common.isWindows
import flank.scripts.ops.dependencies.testfiles.testFilesPath
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeFalse
import org.junit.Test
import java.io.File

class UpdatePluginsTest {
    private val testReport = File("${testFilesPath}report.json")
    private val testPlugins = File("${testFilesPath}TestPlugins")
    private val testVersions = File("${testFilesPath}TestVersions")

    @Test
    fun `should update plugin versions`() {
        // assume
        assumeFalse(isWindows)

        // given
        val expectedVersions =
            File("${testFilesPath}ExpectedVersionAfterPluginDependencies")
        val copyOfTestVersions =
            testVersions.copyTo(
                File("${testFilesPath}VersionsAfterPluginDependencies")
            )
        prepareBuildGradleForTest()

        // when
        testReport.updatePlugins(testPlugins, copyOfTestVersions, testFilesPath)

        // then
        assertEquals(expectedVersions.readText(), copyOfTestVersions.readText())

        // then
        makeBuildGradleNotPrepared()

        // clean
        copyOfTestVersions.delete()
    }

    private fun prepareBuildGradleForTest() {
        File("${testFilesPath}build.gradle.kts.test")
            .renameTo(File("${testFilesPath}build.gradle.kts"))
    }

    private fun makeBuildGradleNotPrepared() {
        File("${testFilesPath}build.gradle.kts")
            .renameTo(File("${testFilesPath}build.gradle.kts.test"))
    }
}
