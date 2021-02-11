package ftl.cli.firebase.test.android.configuration

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import ftl.android.AndroidCatalog
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import picocli.CommandLine

class AndroidLocalesListCommandTest {

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun `should execute AndroidCatalog localesAsTable when run AndroidLocalesListCommand`() {
        mockkObject(AndroidCatalog) {
            CommandLine(AndroidLocalesListCommand()).execute()
            verify { AndroidCatalog.localesAsTable(any()) }
        }
    }

    @Test
    fun androidLocalesListCommandShouldParseConfig() {
        val cmd = AndroidLocalesListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertThat(cmd.configPath).isEqualTo("a")
    }

    @Test
    fun `should not print version information`() {
        AndroidLocalesListCommand().run()
        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).doesNotContainMatch("version: .*")
    }
}
