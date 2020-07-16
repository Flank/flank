package ftl.cli.firebase.test.android.versions

import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants
import org.junit.Test
import picocli.CommandLine

class AndroidVersionsListCommandTest {

    @Test
    fun androidVersionsListCommandOptions() {
        val cmd = AndroidVersionsListCommand()
        assertThat(cmd.configPath).isEqualTo(FtlConstants.defaultAndroidConfig)
        cmd.configPath = "tmp"
        assertThat(cmd.configPath).isEqualTo("tmp")

        assertThat(cmd.usageHelpRequested).isFalse()
        cmd.usageHelpRequested = true
        assertThat(cmd.usageHelpRequested).isTrue()
    }

    @Test
    fun androidVersionsListCommandShouldParseConfig() {
        val cmd = AndroidVersionsListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertThat(cmd.configPath).isEqualTo("a")
    }
}
