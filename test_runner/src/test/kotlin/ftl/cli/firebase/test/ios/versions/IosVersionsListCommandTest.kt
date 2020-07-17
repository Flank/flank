package ftl.cli.firebase.test.ios.versions

import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants
import org.junit.Test
import picocli.CommandLine

class IosVersionsListCommandTest {

    @Test
    fun iosVersionsListCommandOptions() {
        val cmd = IosVersionsListCommand()
        assertThat(cmd.configPath).isEqualTo(FtlConstants.defaultIosConfig)
        cmd.configPath = "tmp"
        assertThat(cmd.configPath).isEqualTo("tmp")

        assertThat(cmd.usageHelpRequested).isFalse()
        cmd.usageHelpRequested = true
        assertThat(cmd.usageHelpRequested).isTrue()
    }

    @Test
    fun iosVersionsListCommandShouldParseConfig() {
        val cmd = IosVersionsListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertThat(cmd.configPath).isEqualTo("a")
    }
}
