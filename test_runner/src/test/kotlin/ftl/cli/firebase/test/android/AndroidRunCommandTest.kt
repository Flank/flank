package ftl.cli.firebase.test.android

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import picocli.CommandLine

@RunWith(FlankTestRunner::class)
class AndroidRunCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun androidRunCommandPrintsHelp() {
        val android = AndroidRunCommand()
        assertThat(android.usageHelpRequested).isFalse()
        CommandLine.run<Runnable>(android, System.out, "-h")

        val output = systemOutRule.log
        Truth.assertThat(output).startsWith(
            "Run tests on Firebase Test Lab\n" +
                "\n" +
                "run [-h] [-c=<configPath>]\n" +
                "\n" +
                "Description:\n" +
                "\n" +
                "Uploads the app and test apk to GCS.\n" +
                "Runs the espresso tests using orchestrator.\n" +
                "Configuration is read from flank.yml\n" +
                "\n" +
                "\n" +
                "Options:\n" +
                "  -c, --config=<configPath>\n" +
                "               YAML config file path\n" +
                "  -h, --help   Prints this help message\n"
        )

        assertThat(android.usageHelpRequested).isTrue()
    }

    @Test
    fun androidRunCommandRuns() {
        AndroidRunCommand().run()
        val output = systemOutRule.log
        Truth.assertThat(output).contains("1 / 1 (100.00%)")
    }
}
