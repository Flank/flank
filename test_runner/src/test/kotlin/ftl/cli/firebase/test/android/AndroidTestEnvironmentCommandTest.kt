package ftl.cli.firebase.test.android

import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants
import ftl.test.util.TestHelper.normalizeLineEnding
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import picocli.CommandLine

class AndroidTestEnvironmentCommandTest {

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun androidTestEnvironmentCommandPrintsHelp() {
        val modelsListCommand = AndroidTestEnvironmentCommand()
        assertThat(modelsListCommand.usageHelpRequested).isFalse()
        CommandLine(modelsListCommand).execute("-h")

        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).startsWith(
            "Print available devices and OS versions list to test against\n" +
                    "\n" +
                    "test-environment [-h] [-c=<configPath>]\n" +
                    "\n" +
                    "Description:\n" +
                    "\n" +
                    "Print available Android devices and Android OS versions list to test against\n" +
                    "\n" +
                    "Options:\n" +
                    "  -c, --config=<configPath>\n" +
                    "               YAML config file path\n" +
                    "  -h, --help   Prints this help message"
        )

        assertThat(modelsListCommand.usageHelpRequested).isTrue()
    }

    @Test
    fun androidTestEnvironmentCommandOptions() {
        val cmd = AndroidTestEnvironmentCommand()
        assertThat(cmd.configPath).isEqualTo(FtlConstants.defaultAndroidConfig)
        cmd.configPath = "tmp"
        assertThat(cmd.configPath).isEqualTo("tmp")

        assertThat(cmd.usageHelpRequested).isFalse()
        cmd.usageHelpRequested = true
        assertThat(cmd.usageHelpRequested).isTrue()
    }

    @Test
    fun androidTestEnvironmentCommandShouldParseConfig() {
        val cmd = AndroidTestEnvironmentCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertThat(cmd.configPath).isEqualTo("a")
    }
}
