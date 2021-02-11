package ftl.cli.firebase.test.ios.configuration

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import ftl.ios.IosCatalog
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import picocli.CommandLine

class IosLocalesListCommandTest {

    @get:Rule
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun `should execute IosCatalog localesAsTable when run IosLocalesListCommand`() {
        mockkObject(IosCatalog) {
            CommandLine(IosLocalesListCommand()).execute()
            verify { IosCatalog.localesAsTable(any()) }
        }
    }

    @Test
    fun iOsLocalesListCommandShouldParseConfig() {
        val cmd = IosLocalesListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertThat(cmd.configPath).isEqualTo("a")
    }

    @Test
    fun `should not print version information`() {
        IosLocalesListCommand().run()
        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).doesNotContainMatch("version: .*")
    }
}
