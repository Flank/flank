package ftl.cli.firebase.test.ios.versions

import com.google.common.truth.Truth.assertThat
import ftl.presentation.cli.firebase.test.ios.versions.IosVersionsListCommand
import org.junit.Test
import picocli.CommandLine

class IosVersionsListCommandTest {

    @Test
    fun iosVersionsListCommandShouldParseConfig() {
        val cmd = IosVersionsListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertThat(cmd.configPath).isEqualTo("a")
    }
}
