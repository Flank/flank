package ftl.cli.firebase

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import picocli.CommandLine

@RunWith(FlankTestRunner::class)
class RefreshCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun refreshCommandPrintsHelp() {
        val refresh = RefreshCommand()
        assertThat(refresh.usageHelpRequested).isFalse()
        CommandLine.run<Runnable>(refresh, System.out, "-h")

        val output = systemOutRule.log
        Truth.assertThat(output).startsWith(
            "Downloads results for the last Firebase Test Lab run\n" +
                    "\n" +
                    "refresh [-h]\n" +
                    "\n" +
                    "Description:\n" +
                    "\n" +
                    "Selects the most recent run in the results/ folder.\n" +
                    "Reads in the matrix_ids.json file. Refreshes any incomplete matrices.\n" +
                    "\n" +
                    "\n" +
                    "Options:\n" +
                    "  -h, --help   Prints this help message\n"
        )

        assertThat(refresh.usageHelpRequested).isTrue()
    }

    @Test
    fun refreshCommandRuns() {
        RefreshCommand().run()
        val output = systemOutRule.log
        Truth.assertThat(output).contains("1 / 1 (100.00%)")
    }

    @Test
    fun refreshCommandOptions() {
        val cmd = RefreshCommand()
        assertThat(cmd.usageHelpRequested).isFalse()
        cmd.usageHelpRequested = true
        assertThat(cmd.usageHelpRequested).isTrue()
    }
}
