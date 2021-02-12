package ftl.cli.firebase.test.ios

import com.google.common.truth.Truth
import org.junit.Test
import picocli.CommandLine

class IosTestEnvironmentCommandTest {

    @Test
    fun iosTestEnvironmentCommandShouldParseConfig() {
        val cmd = IosTestEnvironmentCommand()
        CommandLine(cmd).parseArgs("--config=a")

        Truth.assertThat(cmd.configPath).isEqualTo("a")
    }
}
