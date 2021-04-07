package ftl.cli.firebase.test.android.versions

import com.google.common.truth.Truth.assertThat
import ftl.presentation.cli.firebase.test.android.versions.AndroidVersionsListCommand
import org.junit.Test
import picocli.CommandLine

class AndroidVersionsListCommandTest {

    @Test
    fun androidVersionsListCommandShouldParseConfig() {
        val cmd = AndroidVersionsListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertThat(cmd.configPath).isEqualTo("a")
    }
}
