package ftl.cli.firebase.test.ios.models

import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants
import org.junit.Test
import picocli.CommandLine

class IosModelsListCommandTest {

    @Test
    fun iosModelsListCommandOptions() {
        val cmd = IosModelsListCommand()
        assertThat(cmd.configPath).isEqualTo(FtlConstants.defaultIosConfig)
        cmd.configPath = "tmp"
        assertThat(cmd.configPath).isEqualTo("tmp")

        assertThat(cmd.usageHelpRequested).isFalse()
        cmd.usageHelpRequested = true
        assertThat(cmd.usageHelpRequested).isTrue()
    }

    @Test
    fun iosModelsListCommandShouldParseConfig() {
        val cmd = IosModelsListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertThat(cmd.configPath).isEqualTo("a")
    }
}
