package flank.common

import io.mockk.InternalPlatformDsl.toStr
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeFalse
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class FilesTest {
    private val linkName = "tmp"
    private val targetName = "../"

    @get:Rule
    val root = TemporaryFolder()

    @Test
    fun `should create symbolic link`() {
        assumeFalse(isWindows)
        // when
        createSymbolicLink(
            link = linkName,
            target = targetName
        )

        // then
        assertTrue(Files.isSymbolicLink(Paths.get(linkName)))
    }

    @Test
    fun `copy directory into another and test`() {
        // when
        val temporaryFolder = TemporaryFolder()
        temporaryFolder.create()
        val folder1 = temporaryFolder.newFolder()
        val folder2 = temporaryFolder.newFolder()

        val tempFolder2 = TemporaryFolder(folder1).also {
            it.create()
        }
        val fileInFolder1 = tempFolder2.newFile()

        copyDirectory(folder1.absolutePath, folder2.absolutePath)

        // then
        val strFile = folder2.absolutePath.toStr().plus(osPathSeperator()).plus(tempFolder2.root.name).plus(osPathSeperator())
            .plus(fileInFolder1.name)
        assertTrue(strFile.fileExists())
        temporaryFolder.delete()
        tempFolder2.delete()
    }

    @Test
    fun `delete directory`() {
        // when
        val file = Files.createTempDirectory(null)
        assertTrue(file.toString().fileExists())
        deleteDirectory(file.toString())
        assertTrue(!file.toString().fileExists())
    }

    @After
    fun tearDown() {
        File(linkName).delete()
    }

    @Test
    fun `Should create symbolic file at desired location`() {
        assumeFalse(isWindows)
        // given
        val testFile = root.newFile("test.file").toPath()
        val expectedDestination = Paths.get(root.newFolder("temp").toString(), "test.link")

        // when
        createLinkToFile(expectedDestination, testFile)

        // then
        assertTrue(Files.isSymbolicLink(expectedDestination))

        // clean
        testFile.toFile().delete()
        expectedDestination.toFile().delete()
    }

    @Test
    fun `Should check if directory contains all needed files`() {
        // given
        val testDirectory = root.newFolder("test").toPath()
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
        assertTrue(resultTrue)
        Assert.assertFalse(resultFalse)

        // clean
        testDirectory.toFile().deleteRecursively()
    }
}
