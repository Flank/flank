package ftl.cli.firebase

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import ftl.cli.firebase.test.android.AndroidRunCommand
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import picocli.CommandLine

@RunWith(FlankTestRunner::class)
class CancelCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @get:Rule
    val exit = ExpectedSystemExit.none()

    @Test
    fun cancelCommandPrintsHelp() {
        val command = CancelCommand()
        assertThat(command.usageHelpRequested).isFalse()
        CommandLine(command).execute("-h")

        val output = systemOutRule.log
        Truth.assertThat(output).startsWith(
                "Cancels the last Firebase Test Lab run\n" +
                        "\n" +
                        "cancel [-h]\n" +
                        "\n" +
                        "Description:\n" +
                        "\n" +
                        "Selects the most recent run in the results/ folder.\n" +
                        "Reads in the matrix_ids.json file. Cancels any incomplete matrices.\n" +
                        "\n" +
                        "\n" +
                        "Options:\n" +
                        "  -h, --help   Prints this help message\n"
        )

        assertThat(command.usageHelpRequested).isTrue()
    }

    @Test
    fun cancelCommandRuns() {
        exit.expectSystemExit()
        val runCmd = AndroidRunCommand()
        runCmd.configPath = "./src/test/kotlin/ftl/fixtures/android.yml"
        runCmd.run()
        CancelCommand().run()
        val output = systemOutRule.log
        Truth.assertThat(output).contains("No matrices to cancel")
    }

    @Test
    fun cancelCommandOptions() {
        val cmd = CancelCommand()
        assertThat(cmd.usageHelpRequested).isFalse()
        cmd.usageHelpRequested = true
        assertThat(cmd.usageHelpRequested).isTrue()
    }
}
