package flank.scripts.utils

import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class FileKtTest {

    private val linkName = "tmp"
    private val targetName = "../"

    @Test
    fun `should create symbolic link`() {

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
