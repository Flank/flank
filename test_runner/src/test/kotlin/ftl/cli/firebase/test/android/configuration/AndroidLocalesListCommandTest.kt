package ftl.cli.firebase.test.android.configuration

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import picocli.CommandLine

class AndroidLocalesListCommandTest {

    @Test
    fun androidLocalesListCommandShouldParseConfig() {
        val cmd = AndroidLocalesListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertThat(cmd.configPath).isEqualTo("a")
    }
}
