package ftl.cli.firebase.test.ios

import com.google.common.truth.Truth.assertThat
import ftl.cli.firebase.test.android.AndroidRunCommand
import ftl.config.Device
import ftl.config.FtlConstants
import ftl.config.FtlConstants.isWindows
import ftl.test.util.FlankTestRunner
import org.junit.Assert.fail
import org.junit.Assume.assumeFalse
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import picocli.CommandLine

@Suppress("TooManyFunctions")
@RunWith(FlankTestRunner::class)
class IosRunCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun iosRunCommandPrintsHelp() {
        val iosRun = IosRunCommand()
        assertThat(iosRun.usageHelpRequested).isFalse()
        CommandLine(iosRun).execute("-h")

        val output = systemOutRule.log
        assertThat(output).startsWith("Run tests on Firebase Test Lab")
        assertThat(output).contains("run [-h]")

        assertThat(iosRun.usageHelpRequested).isTrue()
    }

    @Test
    fun iosRunCommandRuns() {
        assumeFalse(isWindows)

        try {
            val runCmd = IosRunCommand()
            runCmd.configPath = "./src/test/kotlin/ftl/fixtures/ios.yml"
            runCmd.run()

            val output = systemOutRule.log
            assertThat(output).contains("1 / 1 (100.00%)")
        } catch (_: Throwable) {
            fail()
        }
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
    fun `empty params parse null`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs()
        assertThat(cmd.dumpShards).isFalse()
        assertThat(cmd.resultsBucket).isNull()
        assertThat(cmd.recordVideo).isNull()
        assertThat(cmd.noRecordVideo).isNull()
        assertThat(cmd.timeout).isNull()
        assertThat(cmd.async).isNull()
        assertThat(cmd.clientDetails).isNull()
        assertThat(cmd.networkProfile).isNull()
        assertThat(cmd.project).isNull()
        assertThat(cmd.resultsHistoryName).isNull()
        assertThat(cmd.maxTestShards).isNull()
        assertThat(cmd.shardTime).isNull()
        assertThat(cmd.repeatTests).isNull()
        assertThat(cmd.testTargetsAlwaysRun).isNull()
        assertThat(cmd.testTargets).isNull()
        assertThat(cmd.filesToDownload).isNull()
        assertThat(cmd.disableSharding).isNull()
        assertThat(cmd.test).isNull()
        assertThat(cmd.xctestrunFile).isNull()
        assertThat(cmd.xcodeVersion).isNull()
        assertThat(cmd.device).isNull()
        assertThat(cmd.resultsDir).isNull()
        assertThat(cmd.flakyTestAttempts).isNull()
        assertThat(cmd.localResultsDir).isNull()
        assertThat(cmd.smartFlankDisableUpload).isNull()
        assertThat(cmd.smartFlankGcsPath).isNull()
        assertThat(cmd.runTimeout).isNull()
    }

    @Test
    fun `resultsBucket parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--results-bucket=a")

        assertThat(cmd.resultsBucket).isEqualTo("a")
    }

    @Test
    fun `recordVideo parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--record-video")

        assertThat(cmd.recordVideo).isTrue()
    }

    @Test
    fun `noRecordVideo parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--no-record-video")

        assertThat(cmd.noRecordVideo).isTrue()
    }

    @Test
    fun `timeout parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--timeout=1m")

        assertThat(cmd.timeout).isEqualTo("1m")
    }

    @Test
    fun `async parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--async")

        assertThat(cmd.async).isTrue()
    }

    @Test
    fun `clientDetails parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--client-details=key1=value1,key2=value2")

        assertThat(cmd.clientDetails).isEqualTo(
            mapOf(
                "key1" to "value1",
                "key2" to "value2"
            )
        )
    }

    @Test
    fun `networkProfile parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--network-profile=a")

        assertThat(cmd.networkProfile).isEqualTo("a")
    }

    @Test
    fun `project parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--project=a")

        assertThat(cmd.project).isEqualTo("a")
    }

    @Test
    fun `resultsHistoryName parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--results-history-name=a")

        assertThat(cmd.resultsHistoryName).isEqualTo("a")
    }

    // flankYml

    @Test
    fun `maxTestShards parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--max-test-shards=3")

        assertThat(cmd.maxTestShards).isEqualTo(3)
    }

    @Test
    fun `repeatTests parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--num-test-runs=3")

        assertThat(cmd.repeatTests).isEqualTo(3)
    }

    @Test
    fun `testTargetsAlwaysRun parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--test-targets-always-run=a,b,c")

        assertThat(cmd.testTargetsAlwaysRun).isEqualTo(arrayListOf("a", "b", "c"))
    }

    @Test
    fun `testTargets parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--test-targets=a,b,c")

        assertThat(cmd.testTargets).isEqualTo(arrayListOf("a", "b", "c"))
    }

    @Test
    fun `test parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--test=a")

        assertThat(cmd.test).isEqualTo("a")
    }

    @Test
    fun `xctestrunFile parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--xctestrun-file=a")

        assertThat(cmd.xctestrunFile).isEqualTo("a")
    }

    @Test
    fun `xcodeVersion parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--xcode-version=999")

        assertThat(cmd.xcodeVersion).isEqualTo("999")
    }

    @Test
    fun `device parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--device=model=iphone8,version=11.2,locale=zh_CN,orientation=default")

        val expectedDevice = Device("iphone8", "11.2", "zh_CN", "default")
        assertThat(cmd.device?.size).isEqualTo(1)
        assertThat(cmd.device?.first()).isEqualTo(expectedDevice)
    }

    @Test
    fun `resultsDir parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--results-dir=a")

        assertThat(cmd.resultsDir).isEqualTo("a")
    }

    @Test
    fun `filesToDownload parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--files-to-download=a,b")

        assertThat(cmd.filesToDownload).isEqualTo(arrayListOf("a", "b"))
    }

    @Test
    fun `flakyTestAttempts parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--num-flaky-test-attempts=10")

        assertThat(cmd.flakyTestAttempts).isEqualTo(10)
    }

    @Test
    fun `shardTime parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--shard-time=99")

        assertThat(cmd.shardTime).isEqualTo(99)
    }

    @Test
    fun `disableShard parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--disable-sharding")

        assertThat(cmd.disableSharding).isEqualTo(true)
    }

    @Test
    fun `local-results-dir parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--local-result-dir=a")

        assertThat(cmd.localResultsDir).isEqualTo("a")
    }

    @Test
    fun `smart-flank-disable-upload parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--smart-flank-disable-upload=true")

        assertThat(cmd.smartFlankDisableUpload).isEqualTo(true)
    }

    @Test
    fun `smart-flank-gcs-path parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--smart-flank-gcs-path=foo")

        assertThat(cmd.smartFlankGcsPath).isEqualTo("foo")
    }

    @Test
    fun `dump-shards parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--dump-shards=true")

        assertThat(cmd.dumpShards).isEqualTo(true)
    }

    @Test
    fun `run-test parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--run-timeout=20s")

        assertThat(cmd.runTimeout).isEqualTo("20s")
    }
}
