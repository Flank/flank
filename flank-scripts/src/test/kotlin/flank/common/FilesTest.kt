package flank.common

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

internal class FilesTest {

    @Test
    fun `Should create symbolic file at desired location`() {
        // given
        val testFile = File.createTempFile("test", "file").toPath()
        val expectedDestination = Paths.get(Files.createTempDirectory("temp").toString(), "test.link")

        // when
        createSymbolicLink(expectedDestination, testFile)

        // then
        Assert.assertTrue(Files.isSymbolicLink(expectedDestination))

        // clean
        testFile.toFile().delete()
        expectedDestination.toFile().delete()
    }

    @Test
    fun `Should download file and store it and destination`() {
        // given
        val testSource = "https://github.com/Flank/flank/blob/master/settings.gradle.kts"
        val testDestination = Paths.get(Files.createTempDirectory("temp").toString(), "settings.gradle.kts")

        // when
        downloadFile(testSource, testDestination)

        // then
        Assert.assertTrue(testDestination.toFile().exists())
        Assert.assertTrue(testDestination.toFile().length() > 0)
    }

    @Test
    fun `Should check if directory contains all needed files`() {
        // given
        val testDirectory = Files.createTempDirectory("test")
        val testFiles = listOf(
            Paths.get(testDirectory.toString(), "testFile1"),
            Paths.get(testDirectory.toString(), "testFile2"),
            Paths.get(testDirectory.toString(), "testFile3"),
            Paths.get(testDirectory.toString(), "testFile4")
        )

        testFiles.forEach { Files.createFile(it) }

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

        // clean
        testDirectory.toFile().deleteRecursively()
    }
}
