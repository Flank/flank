package flank.scripts.dependencies.update

import org.junit.Assert.assertEquals
import org.junit.Test
import skipIfWindows
import java.io.File

class UpdateVersionsInFileTest {

    private val testVersions = File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/TestVersions")

    @Test
    fun `should properly update versions file`() {
        // assume
        skipIfWindows()

        // given
        val copyOfTestVersions = testVersions.copyTo(File(testVersions.absolutePath + "After"))
        val expectedFileAfterChanged =
            File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/ExpectedTestVersionsAfterUpdateVersion")
        val dependenciesToUpdate = listOf(
            DependencyUpdate("DD_PLIST", "DD_PLIST", "1.23", "3.21"),
            DependencyUpdate("DETEKT", "DETEKT", "1.11.0", "0.11.1"),
            DependencyUpdate("PICOCLI", "PICOCLI", "4.4.0", "0.4.4"),
            DependencyUpdate("JACKSON", "JACKSON", "2.11.0", "0.11.2"),
            DependencyUpdate("LOGBACK", "LOGBACK", "1.2.3", "3.2.1")
        )

        // when
        copyOfTestVersions.updateVersions(dependenciesToUpdate)

        // then
        assertEquals(copyOfTestVersions.readText(), expectedFileAfterChanged.readText())

        // clean up
        copyOfTestVersions.delete()
    }
}
