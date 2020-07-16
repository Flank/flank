package ftl.cli.firebase.test.android.models

import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants
import org.junit.Test
import picocli.CommandLine

class AndroidModelsListCommandTest {

    @Test
    fun androidModelsListCommandOptions() {
        val cmd = AndroidModelsListCommand()
        assertThat(cmd.configPath).isEqualTo(FtlConstants.defaultAndroidConfig)
        cmd.configPath = "tmp"
        assertThat(cmd.configPath).isEqualTo("tmp")

        assertThat(cmd.usageHelpRequested).isFalse()
        cmd.usageHelpRequested = true
        assertThat(cmd.usageHelpRequested).isTrue()
    }

    @Test
    fun androidModelsListCommandShouldParseConfig() {
        val cmd = AndroidModelsListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertThat(cmd.configPath).isEqualTo("a")
    }
}
