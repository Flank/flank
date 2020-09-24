package flank.scripts.utils

import org.junit.Assert
import org.junit.Test
import java.io.File


internal class ZipKtTest {

    @Test
    fun zipTest() {
        val src = File("./src")
        val src2 = File("./src2")
        val srcZip = File("./src.zip")
        zip(src, srcZip)
        unzip(srcZip, src2)
        Assert.assertEquals(
            src.calculateSize(),
            src2.calculateSize()
        )
        srcZip.delete()
        src2.deleteRecursively()
    }
}

private fun File.calculateSize(): Long = when {
    isHidden -> 0L
    isDirectory -> listFiles()
        ?.fold(0L) { sum, file -> sum + file.calculateSize() }
        ?: 0L
    else -> length()
}
