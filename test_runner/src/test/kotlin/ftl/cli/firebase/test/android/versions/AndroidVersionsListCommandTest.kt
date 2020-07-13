package ftl.cli.firebase.test.android.versions

import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants
import ftl.test.util.TestHelper.normalizeLineEnding
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import picocli.CommandLine

class AndroidVersionsListCommandTest {

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun androidVersionsListCommandPrintsHelp() {
        val modelsListCommand = AndroidVersionsListCommand()
        assertThat(modelsListCommand.usageHelpRequested).isFalse()
        CommandLine(modelsListCommand).execute("-h")

        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).startsWith(
            "List of OS versions available to test against\n" +
                    "\n" +
                    "list [-h] [-c=<configPath>]\n" +
                    "\n" +
                    "Description:\n" +
                    "\n" +
                    "Print current list of Android OS versions available to test against\n" +
                    "\n" +
                    "Options:\n" +
                    "  -c, --config=<configPath>\n" +
                    "               YAML config file path\n" +
                    "  -h, --help   Prints this help message"
        )

        assertThat(modelsListCommand.usageHelpRequested).isTrue()
    }

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
