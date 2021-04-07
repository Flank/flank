package ftl.cli.firebase

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import ftl.presentation.cli.firebase.CancelCommand
import ftl.presentation.cli.firebase.test.android.AndroidRunCommand
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import picocli.CommandLine

@RunWith(FlankTestRunner::class)
class CancelCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun cancelCommandPrintsHelp() {
        val command = CancelCommand()
        assertThat(command.usageHelpRequested).isFalse()
        CommandLine(command).execute("-h")

        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).startsWith(
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
        val runCmd = AndroidRunCommand()
        runCmd.configPath = "./src/test/kotlin/ftl/fixtures/simple-android-flank.yml"
        runCmd.run()
        CancelCommand().run()
        val output = systemOutRule.log
        assertThat(output).contains("No matrices to cancel")
    }

    @Test
    fun cancelCommandOptions() {
        val cmd = CancelCommand()
        assertThat(cmd.usageHelpRequested).isFalse()
        cmd.usageHelpRequested = true
        assertThat(cmd.usageHelpRequested).isTrue()
    }
}
