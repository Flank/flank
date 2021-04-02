package ftl.cli.firebase.test.ios.configuration

import com.google.common.truth.Truth
import ftl.ios.IosCatalog
import ftl.presentation.cli.firebase.test.ios.configuration.IosLocalesListCommand
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Test
import picocli.CommandLine

class IosLocalesListCommandTest {

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

        Truth.assertThat(cmd.configPath).isEqualTo("a")
    }
}
