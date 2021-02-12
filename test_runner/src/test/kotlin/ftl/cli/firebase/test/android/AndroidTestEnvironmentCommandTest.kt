package ftl.cli.firebase.test.android

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import picocli.CommandLine

class AndroidTestEnvironmentCommandTest {
    @Test
    fun androidTestEnvironmentCommandShouldParseConfig() {
        val cmd = AndroidTestEnvironmentCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertThat(cmd.configPath).isEqualTo("a")
    }
}
