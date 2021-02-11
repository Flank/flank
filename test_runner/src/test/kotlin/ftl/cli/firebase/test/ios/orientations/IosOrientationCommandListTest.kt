package ftl.cli.firebase.test.ios.orientations

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import ftl.ios.IosCatalog
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import picocli.CommandLine

class IosOrientationCommandListTest {

    @get:Rule
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun `should execute IosCatalog supportedOrientationsAsTable when run IosOrientationsListCommand`() {
        mockkObject(IosCatalog) {
            CommandLine(IosOrientationsListCommand()).execute()
            verify { IosCatalog.supportedOrientationsAsTable(any()) }
        }
    }

    @Test
    fun `IosOrientationsListCommand should parse config`() {
        val cmd = IosOrientationsListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertEquals(cmd.configPath, "a")
    }

    @Test
    fun `should not print version information`() {
        IosOrientationsListCommand().run()
        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).doesNotContainMatch("version: .*")
    }
}
