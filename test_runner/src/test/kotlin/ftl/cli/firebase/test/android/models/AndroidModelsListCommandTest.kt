package ftl.cli.firebase.test.android.models

import com.google.common.truth.Truth.assertThat
import ftl.presentation.cli.firebase.test.android.models.AndroidModelsListCommand
import org.junit.Test
import picocli.CommandLine

class AndroidModelsListCommandTest {

    @Test
    fun androidModelsListCommandShouldParseConfig() {
        val cmd = AndroidModelsListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertThat(cmd.configPath).isEqualTo("a")
    }
}
