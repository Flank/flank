package flank.scripts.ops.dependencies

import flank.common.isWindows
import flank.scripts.ops.dependencies.testfiles.testFilesPath
import flank.scripts.utils.toDependencyUpdate
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeFalse
import org.junit.Test
import java.io.File

class UpdateVersionsInFileTest {

    private val testVersions = File("${testFilesPath}TestVersions")

    @Test
    fun `should properly update versions file`() {
        // assume
        assumeFalse(isWindows)

        // given
        val copyOfTestVersions = testVersions.copyTo(File(testVersions.absolutePath + "After"))
        val expectedFileAfterChanged =
            File("${testFilesPath}ExpectedTestVersionsAfterUpdateVersion")
        val dependenciesToUpdate = listOf(
            toDependencyUpdate("DD_PLIST", "DD_PLIST", "1.23", "3.21"),
            toDependencyUpdate("DETEKT", "DETEKT", "1.11.0", "0.11.1"),
            toDependencyUpdate("PICOCLI", "PICOCLI", "4.4.0", "0.4.4"),
            toDependencyUpdate("JACKSON", "JACKSON", "2.11.0", "0.11.2"),
            toDependencyUpdate("LOGBACK", "LOGBACK", "1.2.3", "3.2.1")
        )

        // when
        copyOfTestVersions.updateVersions(dependenciesToUpdate)

        // then
        assertEquals(copyOfTestVersions.readText(), expectedFileAfterChanged.readText())

        // clean up
        copyOfTestVersions.delete()
    }
}
