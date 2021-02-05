package ftl.ios.xctest.common

import flank.common.appDataDirectory
import flank.common.hasAllFiles
import flank.common.isMacOS
import flank.common.isWindows
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.nio.file.Paths

internal class InstallParseBinariesTest {

    @Test
    fun `should install binaries for windows`() {
        // given
        assumeTrue(isWindows)

        // when
        installBinaries

        // then
        assertTrue(
            Paths.get(appDataDirectory, ".flank").toFile().hasAllFiles(
                listOf("libatomic.so.1", "libatomic.so.1.2.0")
            )
        )
    }

    @Test
    fun `should install binaries for linux`() {
        // given
        assumeTrue(isWindows.not() && isMacOS.not())
        println("asd")
        // when
        installBinaries

        // then
        assertTrue(
            Paths.get(appDataDirectory, ".flank").toFile().hasAllFiles(
                listOf("nm", "swift-demangle", "libatomic.so.1", "libatomic.so.1.2.0")
            )
        )
    }
}
