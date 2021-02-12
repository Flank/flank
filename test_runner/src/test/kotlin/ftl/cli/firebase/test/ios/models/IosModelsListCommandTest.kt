package ftl.cli.firebase.test.ios.models

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import picocli.CommandLine

class IosModelsListCommandTest {
    @Test
    fun iosModelsListCommandShouldParseConfig() {
        val cmd = IosModelsListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertThat(cmd.configPath).isEqualTo("a")
    }
}
