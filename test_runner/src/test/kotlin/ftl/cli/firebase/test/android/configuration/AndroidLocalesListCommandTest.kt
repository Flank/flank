package ftl.cli.firebase.test.android.configuration

import com.google.common.truth.Truth.assertThat
import ftl.android.AndroidCatalog
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Test
import picocli.CommandLine

class AndroidLocalesListCommandTest {

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
}
