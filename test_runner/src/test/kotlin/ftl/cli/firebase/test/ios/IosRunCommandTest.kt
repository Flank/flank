package ftl.cli.firebase.test.ios

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
class IosRunCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @get:Rule
    val exit = ExpectedSystemExit.none()!!

    @Test
    fun iosRunCommandPrintsHelp() {
        val iosRun = IosRunCommand()
        assertThat(iosRun.usageHelpRequested).isFalse()
        CommandLine.run<Runnable>(iosRun, System.out, "-h")

        val output = systemOutRule.log
        Truth.assertThat(output).startsWith("Run tests on Firebase Test Lab")
        Truth.assertThat(output).contains("run [-h]")

        assertThat(iosRun.usageHelpRequested).isTrue()
    }

    @Test
    fun iosRunCommandRuns() {
        exit.expectSystemExit()
        IosRunCommand().run()
        val output = systemOutRule.log
        Truth.assertThat(output).contains("1 / 1 (100.00%)")
    }

    @Test
    fun iosRunCommandOptions() {
        val cmd = IosRunCommand()
        assertThat(cmd.configPath).isEqualTo(FtlConstants.defaultIosConfig)
        cmd.configPath = "tmp"
        assertThat(cmd.configPath).isEqualTo("tmp")

        assertThat(cmd.usageHelpRequested).isFalse()
        cmd.usageHelpRequested = true
        assertThat(cmd.usageHelpRequested).isTrue()
    }

    @Test
    fun empty_params_parse_null() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parse()
        assertThat(cmd.resultsBucket).isNull()
        assertThat(cmd.recordVideo).isNull()
        assertThat(cmd.noRecordVideo).isNull()
        assertThat(cmd.timeout).isNull()
    }

    @Test
    fun resultsBucket_parse() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parse("--results-bucket=a")

        assertThat(cmd.resultsBucket).isEqualTo("a")
    }

    @Test
    fun recordVideo_parse() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parse("--record-video")

        assertThat(cmd.recordVideo).isTrue()
    }

    @Test
    fun noRecordVideo_parse() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parse("--no-record-video")

        assertThat(cmd.noRecordVideo).isTrue()
    }

    @Test
    fun timeout_parse() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parse("--timeout=1m")

        assertThat(cmd.timeout).isEqualTo("1m")
    }
}
