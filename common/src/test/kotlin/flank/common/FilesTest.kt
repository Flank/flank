package flank.common

import io.mockk.InternalPlatformDsl.toStr
import org.junit.After
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
    val folder = TemporaryFolder()

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
        val strFile =
            folder2.absolutePath.toStr().plus(osPathSeperator()).plus(tempFolder2.root.name).plus(osPathSeperator())
                .plus(fileInFolder1.name)
        assertTrue(strFile.fileExists())
        temporaryFolder.delete()
        tempFolder2.delete()
    }

    @Test
    fun `delete directory`() {
        // when
        val file = folder.newFolder("anyPath")
        assertTrue(file.toString().fileExists())
        deleteDirectory(file.toString())
        assertTrue(!file.toString().fileExists())
    }

    @After
    fun tearDown() {
        File(linkName).delete()
    }
}
