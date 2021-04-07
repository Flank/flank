package ftl.cli.firebase.test.ios

import com.google.common.truth.Truth.assertThat
import flank.common.isWindows
import flank.common.normalizeLineEnding
import ftl.args.IosArgs
import ftl.config.Device
import ftl.config.FtlConstants
import ftl.gc.GcStorage
import ftl.presentation.cli.firebase.test.ios.IosRunCommand
import ftl.run.IOS_SHARD_FILE
import ftl.run.dumpShards
import ftl.test.util.FlankTestRunner
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
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

        val runCmd = IosRunCommand()
        runCmd.configPath = "./src/test/kotlin/ftl/fixtures/ios.yml"
        runCmd.run()

        val output = systemOutRule.log
        assertThat(output).contains("1 / 1 (100.00%)")
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
        assertThat(cmd.obfuscate).isFalse()
        assertThat(cmd.config.common.gcloud.resultsBucket).isNull()
        assertThat(cmd.config.common.gcloud.recordVideo).isNull()
        assertThat(cmd.config.common.gcloud.timeout).isNull()
        assertThat(cmd.config.common.gcloud.async).isNull()
        assertThat(cmd.config.common.gcloud.clientDetails).isNull()
        assertThat(cmd.config.common.gcloud.networkProfile).isNull()
        assertThat(cmd.config.common.flank.project).isNull()
        assertThat(cmd.config.common.gcloud.resultsHistoryName).isNull()
        assertThat(cmd.config.common.flank.maxTestShards).isNull()
        assertThat(cmd.config.common.flank.shardTime).isNull()
        assertThat(cmd.config.common.flank.repeatTests).isNull()
        assertThat(cmd.config.common.flank.testTargetsAlwaysRun).isNull()
        assertThat(cmd.config.platform.flank.testTargets).isNull()
        assertThat(cmd.config.common.flank.filesToDownload).isNull()
        assertThat(cmd.config.common.flank.disableSharding).isNull()
        assertThat(cmd.config.platform.gcloud.test).isNull()
        assertThat(cmd.config.platform.gcloud.xctestrunFile).isNull()
        assertThat(cmd.config.platform.gcloud.xcodeVersion).isNull()
        assertThat(cmd.config.common.gcloud.devices).isNull()
        assertThat(cmd.config.common.gcloud.resultsDir).isNull()
        assertThat(cmd.config.common.gcloud.flakyTestAttempts).isNull()
        assertThat(cmd.config.common.gcloud.directoriesToPull).isNull()
        assertThat(cmd.config.common.flank.localResultsDir).isNull()
        assertThat(cmd.config.common.flank.smartFlankDisableUpload).isNull()
        assertThat(cmd.config.common.flank.smartFlankGcsPath).isNull()
        assertThat(cmd.config.common.flank.runTimeout).isNull()
        assertThat(cmd.config.common.gcloud.otherFiles).isNull()
    }

    @Test
    fun `resultsBucket parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--results-bucket=a")

        assertThat(cmd.config.common.gcloud.resultsBucket).isEqualTo("a")
    }

    @Test
    fun `recordVideo parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--record-video")

        assertThat(cmd.config.common.gcloud.recordVideo).isTrue()
    }

    @Test
    fun `noRecordVideo parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--no-record-video")

        assertThat(cmd.config.common.gcloud.recordVideo).isFalse()
    }

    @Test
    fun `timeout parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--timeout=1m")

        assertThat(cmd.config.common.gcloud.timeout).isEqualTo("1m")
    }

    @Test
    fun `async parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--async")

        assertThat(cmd.config.common.gcloud.async).isTrue()
    }

    @Test
    fun `clientDetails parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--client-details=key1=value1,key2=value2")

        assertThat(cmd.config.common.gcloud.clientDetails).isEqualTo(
            mapOf(
                "key1" to "value1",
                "key2" to "value2"
            )
        )
    }

    @Test
    fun `networkProfile parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--network-profile=a")

        assertThat(cmd.config.common.gcloud.networkProfile).isEqualTo("a")
    }

    @Test
    fun `project parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--project=a")

        assertThat(cmd.config.common.flank.project).isEqualTo("a")
    }

    @Test
    fun `resultsHistoryName parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--results-history-name=a")

        assertThat(cmd.config.common.gcloud.resultsHistoryName).isEqualTo("a")
    }

    // flankYml

    @Test
    fun `maxTestShards parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--max-test-shards=3")

        assertThat(cmd.config.common.flank.maxTestShards).isEqualTo(3)
    }

    @Test
    fun `repeatTests parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--num-test-runs=3")

        assertThat(cmd.config.common.flank.repeatTests).isEqualTo(3)
    }

    @Test
    fun `testTargetsAlwaysRun parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--test-targets-always-run=a,b,c")

        assertThat(cmd.config.common.flank.testTargetsAlwaysRun).isEqualTo(arrayListOf("a", "b", "c"))
    }

    @Test
    fun `testTargets parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--test-targets=a,b,c")

        assertThat(cmd.config.platform.flank.testTargets).isEqualTo(arrayListOf("a", "b", "c"))
    }

    @Test
    fun `test parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--test=a")

        assertThat(cmd.config.platform.gcloud.test).isEqualTo("a")
    }

    @Test
    fun `xctestrunFile parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--xctestrun-file=a")

        assertThat(cmd.config.platform.gcloud.xctestrunFile).isEqualTo("a")
    }

    @Test
    fun `xcodeVersion parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--xcode-version=999")

        assertThat(cmd.config.platform.gcloud.xcodeVersion).isEqualTo("999")
    }

    @Test
    fun `device parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--device=model=iphone8,version=11.2,locale=zh_CN,orientation=default")

        val expectedDevice = Device("iphone8", "11.2", "zh_CN", "default")
        assertThat(cmd.config.common.gcloud.devices?.size).isEqualTo(1)
        assertThat(cmd.config.common.gcloud.devices?.first()).isEqualTo(expectedDevice)
    }

    @Test
    fun `resultsDir parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--results-dir=a")

        assertThat(cmd.config.common.gcloud.resultsDir).isEqualTo("a")
    }

    @Test
    fun `filesToDownload parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--files-to-download=a,b")

        assertThat(cmd.config.common.flank.filesToDownload).isEqualTo(arrayListOf("a", "b"))
    }

    @Test
    fun `flakyTestAttempts parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--num-flaky-test-attempts=10")

        assertThat(cmd.config.common.gcloud.flakyTestAttempts).isEqualTo(10)
    }

    @Test
    fun `shardTime parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--shard-time=99")

        assertThat(cmd.config.common.flank.shardTime).isEqualTo(99)
    }

    @Test
    fun `disableShard parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--disable-sharding")

        assertThat(cmd.config.common.flank.disableSharding).isEqualTo(true)
    }

    @Test
    fun `local-results-dir parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--local-result-dir=a")

        assertThat(cmd.config.common.flank.localResultsDir).isEqualTo("a")
    }

    @Test
    fun `smart-flank-disable-upload parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--smart-flank-disable-upload=true")

        assertThat(cmd.config.common.flank.smartFlankDisableUpload).isEqualTo(true)
    }

    @Test
    fun `smart-flank-gcs-path parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--smart-flank-gcs-path=foo")

        assertThat(cmd.config.common.flank.smartFlankGcsPath).isEqualTo("foo")
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

        assertThat(cmd.config.common.flank.runTimeout).isEqualTo("20s")
    }

    @Test
    fun `obfuscate parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--obfuscate")

        assertThat(cmd.obfuscate).isTrue()
    }

    @Test
    fun `default-test-time parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--default-test-time=15")

        assertThat(cmd.config.common.flank.defaultTestTime).isEqualTo(15.0)
    }

    @Test
    fun `use-average-test-time-for-new-tests parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--use-average-test-time-for-new-tests")

        assertThat(cmd.config.common.flank.useAverageTestTimeForNewTests).isTrue()
    }

    @Test
    fun `--output-report parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--output-report=json")

        assertThat(cmd.config.common.flank.outputReport).isEqualTo("json")
    }

    @Test
    fun `should dump shards on ios test run`() {
        assumeFalse(isWindows) // TODO remove in #1180

        mockkStatic("ftl.run.DumpShardsKt")
        val runCmd = IosRunCommand()
        runCmd.configPath = "./src/test/kotlin/ftl/fixtures/simple-ios-flank.yml"
        runCmd.run()
        verify { any<IosArgs>().dumpShards(any()) }
    }

    @Test
    fun `should dump shards on ios test run and not upload when disable-upload-results set`() {
        assumeFalse(isWindows) // TODO remove in #1180

        mockkStatic("ftl.run.DumpShardsKt")
        mockkObject(GcStorage) {
            val runCmd = IosRunCommand()
            runCmd.configPath = "./src/test/kotlin/ftl/fixtures/simple-ios-flank.yml"
            CommandLine(runCmd).parseArgs("--disable-results-upload")
            runCmd.run()
            verify { any<IosArgs>().dumpShards(any()) }
            verify(inverse = true) { GcStorage.upload(IOS_SHARD_FILE, any(), any()) }
        }
    }

    @Test
    fun `otherFiles parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--other-files=a=1,b=2")

        assertThat(cmd.config.common.gcloud.otherFiles).hasSize(2)
    }

    @Test
    fun `additionalApks parse`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--additional-ipas=a.ipa,b.ipa")
        println(cmd.config.platform.gcloud.additionalIpas)

        assertThat(cmd.config.platform.gcloud.additionalIpas).hasSize(2)
        assertThat(cmd.config.platform.gcloud.additionalIpas).isEqualTo(listOf("a.ipa", "b.ipa"))
    }

    @Test
    fun `should properly parse type`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--type=a")

        assertThat(cmd.config.common.gcloud.type).isEqualTo("a")
    }

    @Test
    fun `should properly parse test-special-entitlements`() {
        val cmd = IosRunCommand()
        CommandLine(cmd).parseArgs("--test-special-entitlements")

        assertThat(cmd.config.platform.gcloud.testSpecialEntitlements).isEqualTo(true)
    }

    @Test
    fun `should print version information`() {
        assumeFalse(isWindows)

        IosRunCommand().run()
        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).containsMatch("version: .*")
    }
}
