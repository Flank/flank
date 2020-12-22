package flank.scripts.dependencies.update

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Test
import skipIfWindows

class UpdatePluginsTest {
    private val testReport = File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/report.json")
    private val testPlugins = File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/TestPlugins")
    private val testVersions = File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/TestVersions")

    @Test
    fun `should update plugin versions`() {
        // assume
        skipIfWindows()

        // given
        val expectedVersions =
            File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/ExpectedVersionAfterPluginDependencies")
        val copyOfTestVersions =
            testVersions.copyTo(
                File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/VersionsAfterPluginDependencies")
            )
        prepareBuildGradleForTest()

        // when
        testReport.updatePlugins(testPlugins, copyOfTestVersions, "src/test/kotlin/flank/scripts/dependencies/update/testfiles")

        // then
        assertEquals(expectedVersions.readText(), copyOfTestVersions.readText())

        // then
        makeBuildGradleNotPrepared()
        copyOfTestVersions.delete()
    }

    private fun prepareBuildGradleForTest() {
        File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/build.gradle.kts.test")
            .renameTo(File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/build.gradle.kts"))
    }

    private fun makeBuildGradleNotPrepared() {
        File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/build.gradle.kts")
            .renameTo(File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/build.gradle.kts.test"))
    }
}
