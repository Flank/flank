package ftl.cli.firebase.test.ios

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import picocli.CommandLine

@RunWith(FlankTestRunner::class)
class IosRefreshCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun iosRefreshCommandPrintsHelp() {
        val refresh = IosRefreshCommand()
        assertThat(refresh.usageHelpRequested).isFalse()
        CommandLine.run<Runnable>(refresh, System.out, "-h")

        val output = systemOutRule.log
        Truth.assertThat(output).startsWith(
            "Downloads results for the last Firebase Test Lab run\n" +
                "\n" +
                "refresh [-h] [-c=<configPath>]\n" +
                "\n" +
                "Description:\n" +
                "\n" +
                "Selects the most recent run in the results/ folder.\n" +
                "Reads in the matrix_ids.json file. Refreshes any incomplete matrices.\n" +
                "\n" +
                "\n" +
                "Options:\n" +
                "  -c, --config=<configPath>\n" +
                "               YAML config file path\n" +
                "  -h, --help   Prints this help message\n"
        )

        assertThat(refresh.usageHelpRequested).isTrue()
    }

    @Test
    fun iosRefreshCommandRuns() {
        IosRefreshCommand().run()
        val output = systemOutRule.log
        Truth.assertThat(output).contains("1 / 1 (100.00%)")
    }

    @Test
    fun iosRefreshCommandOptions() {
        val cmd = IosRefreshCommand()
        assertThat(cmd.configPath).isEqualTo(FtlConstants.defaultIosConfig)
        cmd.configPath = "tmp"
        assertThat(cmd.configPath).isEqualTo("tmp")

        assertThat(cmd.usageHelpRequested).isFalse()
        cmd.usageHelpRequested = true
        assertThat(cmd.usageHelpRequested).isTrue()
    }
}
