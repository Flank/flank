package ftl.cli.firebase.test.android

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import picocli.CommandLine

@RunWith(FlankTestRunner::class)
class AndroidRunCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @get:Rule
    val exit = ExpectedSystemExit.none()

    @Test
    fun androidRunCommandPrintsHelp() {
        val android = AndroidRunCommand()
        assertThat(android.usageHelpRequested).isFalse()
        CommandLine.run<Runnable>(android, System.out, "-h")

        val output = systemOutRule.log
        Truth.assertThat(output).startsWith(
            """
                Run tests on Firebase Test Lab

                run [-h] [--app=<app>] [-c=<configPath>]

                Description:

                Uploads the app and test apk to GCS.
                Runs the espresso tests using orchestrator.
                Configuration is read from flank.yml


                Options:
                  -c, --config=<configPath>
                                    YAML config file path
                  -h, --help        Prints this help message
                      --app=<app>   The path to the application binary file.
            """.trimIndent()
        )

        assertThat(android.usageHelpRequested).isTrue()
    }

    @Test
    fun androidRunCommandRuns() {
        exit.expectSystemExit()
        AndroidRunCommand().run()
        val output = systemOutRule.log
        assertThat(output).contains("1 / 1 (100.00%)")
    }

    @Test
    fun androidRunCommandOptions() {
        val cmd = AndroidRunCommand()
        assertThat(cmd.configPath).isEqualTo(FtlConstants.defaultAndroidConfig)
        cmd.configPath = "tmp"
        assertThat(cmd.configPath).isEqualTo("tmp")

        assertThat(cmd.usageHelpRequested).isFalse()
        cmd.usageHelpRequested = true
        assertThat(cmd.usageHelpRequested).isTrue()
    }

    @Test
    fun app_parse() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parse("--app", "myApp.apk")
        assertThat(cmd.app).isEqualTo("myApp.apk")
    }

    @Test
    fun app_parse_null() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parse()
        assertThat(cmd.app).isEqualTo(null)
    }
}
