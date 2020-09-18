package flank.scripts.dependencies.update

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class UpdatePluginsTest {
    private val testReport = File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/report.json")
    private val testPlugins = File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/TestPlugins")
    private val testVersions = File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/TestVersions")

    @Test
    fun `should update plugin versions`() {
        // given
        val expectedVersions =
            File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/ExpectedVersionAfterPluginDependencies")
        val copyOfTestVersions =
            testVersions.copyTo(
                File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/VersionsAfterPluginDependencies"))
        prepareBuildGradleForTest()

        // when
        testReport.updatePlugins(testPlugins, copyOfTestVersions, "src/test/kotlin/flank/scripts/dependencies/update/testfiles")

        // then
        assertEquals(copyOfTestVersions.readText(), expectedVersions.readText())

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
