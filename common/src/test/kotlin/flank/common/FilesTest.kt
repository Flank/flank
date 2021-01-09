package flank.common

import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeFalse
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class FilesTest {

    private val linkName = "tmp"
    private val targetName = "../"

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

    @After
    fun tearDown() {
        File(linkName).delete()
    }
}
