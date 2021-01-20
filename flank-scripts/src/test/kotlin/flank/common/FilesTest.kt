package flank.common

import org.junit.Assert
import org.junit.Assume
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.nio.file.Files
import java.nio.file.Paths

internal class FilesTest {

    @get:Rule
    val folder = TemporaryFolder()

    @Test
    fun `Should create symbolic file at desired location`() {
        Assume.assumeFalse(isWindows)
        // given
        val testFile = folder.newFile("test.file").toPath()
        val expectedDestination = Paths.get(folder.newFolder("temp").toString(), "test.link")

        // when
        createSymbolicLinkToFile(expectedDestination, testFile)

        // then
        Assert.assertTrue(Files.isSymbolicLink(expectedDestination))

        // clean
        testFile.toFile().delete()
        expectedDestination.toFile().delete()
    }

    @Test
    fun `Should download file and store it and destination`() {
        // given
        val testSource = "https://raw.githubusercontent.com/Flank/flank/master/settings.gradle.kts"
        val testDestination = Paths.get(folder.root.absolutePath, "settings.gradle.kts")

        // when
        downloadFile(testSource, testDestination)

        // then
        Assert.assertTrue(testDestination.toFile().exists())
        Assert.assertTrue(testDestination.toFile().length() > 0)
    }

    @Test
    fun `Should check if directory contains all needed files`() {
        // given
        val testDirectory = folder.root.absolutePath
        val testFiles = listOf(
            Paths.get(testDirectory.toString(), "testFile1"),
            Paths.get(testDirectory.toString(), "testFile2"),
            Paths.get(testDirectory.toString(), "testFile3"),
            Paths.get(testDirectory.toString(), "testFile4")
        )

        testFiles.forEach { folder.newFile(it.fileName.toString()) }

        // when
        val resultTrue = testDirectory.toFile().hasAllFiles(testFiles.map { it.fileName.toString() })
        val resultFalse = testDirectory.toFile()
            .hasAllFiles(
                (testFiles + Paths.get(testDirectory.toString(), "testFile5"))
                    .map { it.fileName.toString() }
            )

        // then
        Assert.assertTrue(resultTrue)
        Assert.assertFalse(resultFalse)
    }
}
