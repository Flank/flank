package ftl.cli.firebase.test.ios

import com.google.common.truth.Truth
import ftl.config.FtlConstants
import ftl.test.util.TestHelper.normalizeLineEnding
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import picocli.CommandLine

class IosTestEnvironmentCommandTest {

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun iosTestEnvironmentCommandPrintsHelp() {
        val modelsListCommand = IosTestEnvironmentCommand()
        Truth.assertThat(modelsListCommand.usageHelpRequested).isFalse()
        CommandLine(modelsListCommand).execute("-h")

        val output = systemOutRule.log.normalizeLineEnding()
        Truth.assertThat(output).startsWith(
            "Print available devices and OS versions list to test against\n" +
                    "\n" +
                    "test-environment [-h] [-c=<configPath>]\n" +
                    "\n" +
                    "Description:\n" +
                    "\n" +
                    "Print available iOS devices and iOS versions list to test against\n" +
                    "\n" +
                    "Options:\n" +
                    "  -c, --config=<configPath>\n" +
                    "               YAML config file path\n" +
                    "  -h, --help   Prints this help message"
        )

        Truth.assertThat(modelsListCommand.usageHelpRequested).isTrue()
    }

    @Test
    fun iosTestEnvironmentCommandOptions() {
        val cmd = IosTestEnvironmentCommand()
        Truth.assertThat(cmd.configPath).isEqualTo(FtlConstants.defaultIosConfig)
        cmd.configPath = "tmp"
        Truth.assertThat(cmd.configPath).isEqualTo("tmp")

        Truth.assertThat(cmd.usageHelpRequested).isFalse()
        cmd.usageHelpRequested = true
        Truth.assertThat(cmd.usageHelpRequested).isTrue()
    }

    @Test
    fun iosTestEnvironmentCommandShouldParseConfig() {
        val cmd = IosTestEnvironmentCommand()
        CommandLine(cmd).parseArgs("--config=a")

        Truth.assertThat(cmd.configPath).isEqualTo("a")
    }
}
