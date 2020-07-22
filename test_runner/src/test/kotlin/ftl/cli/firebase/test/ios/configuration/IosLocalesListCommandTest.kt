package ftl.cli.firebase.test.ios.configuration

import com.google.common.truth.Truth
import org.junit.Test
import picocli.CommandLine

class IosLocalesListCommandTest {

    @Test
    fun androidLocalesListCommandShouldParseConfig() {
        val cmd = IosLocalesListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        Truth.assertThat(cmd.configPath).isEqualTo("a")
    }
}
