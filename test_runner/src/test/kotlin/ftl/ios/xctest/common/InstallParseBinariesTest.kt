package ftl.ios.xctest.common

import ftl.config.FtlConstants
import ftl.util.hasAllFiles
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.nio.file.Paths

internal class InstallParseBinariesTest {

    @Test
    fun `should install binaries for windows`() {
        // given
        assumeTrue(FtlConstants.isWindows)

        // when
        installBinaries

        // then
        assertTrue(
            Paths.get(FtlConstants.userHome, ".flank").toFile().hasAllFiles(
                listOf("libatomic.so.1", "libatomic.so.1.2.0")
            )
        )
    }

    @Test
    fun `should install binaries for linux`() {
        // given
        assumeTrue(FtlConstants.isWindows.not() && FtlConstants.isMacOS.not())

        // when
        installBinaries

        // then
        assertTrue(
            Paths.get(FtlConstants.userHome, ".flank").toFile().hasAllFiles(
                listOf("nm", "swift-demangle", "libatomic.so.1", "libatomic.so.1.2.0")
            )
        )
    }
}
