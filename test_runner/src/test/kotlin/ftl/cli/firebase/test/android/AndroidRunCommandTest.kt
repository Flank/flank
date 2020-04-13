package ftl.cli.firebase.test.android

import com.google.common.truth.Truth.assertThat
import ftl.args.yml.AppTestPair
import ftl.config.Device
import ftl.config.FtlConstants
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import picocli.CommandLine

@Suppress("TooManyFunctions")
@RunWith(FlankTestRunner::class)
class AndroidRunCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun androidRunCommandPrintsHelp() {
        val android = AndroidRunCommand()
        assertThat(android.usageHelpRequested).isFalse()
        CommandLine(android).execute("-h")

        val output = systemOutRule.log
        assertThat(output).startsWith("Run tests on Firebase Test Lab")
        assertThat(output).contains("run [-h]")

        assertThat(android.usageHelpRequested).isTrue()
    }

    @Test
    fun androidRunCommandRuns() {
        val runCmd = AndroidRunCommand()
        runCmd.configPath = "./src/test/kotlin/ftl/fixtures/simple-android-flank.yml"
        runCmd.run()
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
    fun `empty params parse null`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs()
        assertThat(cmd.dumpShards).isFalse()
        assertThat(cmd.dryRun).isFalse()
        assertThat(cmd.app).isNull()
        assertThat(cmd.test).isNull()
        assertThat(cmd.additionalApks).isNull()
        assertThat(cmd.testTargets).isNull()
        assertThat(cmd.useOrchestrator).isNull()
        assertThat(cmd.noUseOrchestrator).isNull()
        assertThat(cmd.autoGoogleLogin).isNull()
        assertThat(cmd.noUseOrchestrator).isNull()
        assertThat(cmd.performanceMetrics).isNull()
        assertThat(cmd.noPerformanceMetrics).isNull()
        assertThat(cmd.numUniformShards).isNull()
        assertThat(cmd.testRunnerClass).isNull()
        assertThat(cmd.environmentVariables).isNull()
        assertThat(cmd.directoriesToPull).isNull()
        assertThat(cmd.otherFiles).isNull()
        assertThat(cmd.device).isNull()
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
        assertThat(cmd.filesToDownload).isNull()
        assertThat(cmd.resultsDir).isNull()
        assertThat(cmd.flakyTestAttempts).isNull()
        assertThat(cmd.disableSharding).isNull()
        assertThat(cmd.localResultsDir).isNull()
        assertThat(cmd.smartFlankDisableUpload).isNull()
        assertThat(cmd.smartFlankGcsPath).isNull()
        assertThat(cmd.additionalAppTestApks).isNull()
        assertThat(cmd.keepFilePath).isNull()
        assertThat(cmd.runTimeout).isNull()
    }

    @Test
    fun `app parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--app", "myApp.apk")
        assertThat(cmd.app).isEqualTo("myApp.apk")
    }

    @Test
    fun `test parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--test", "myTestApp.apk")
        assertThat(cmd.test).isEqualTo("myTestApp.apk")
    }

    @Test
    fun `additionalApks parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--additional-apks=a.apk,b.apk")
        assertThat(cmd.additionalApks).isEqualTo(listOf("a.apk", "b.apk"))
    }

    @Test
    fun `testTargets parse`() {
        val testTargets = "--test-targets"
        val params = arrayOf(testTargets, "class com.foo.Clazz", testTargets, "package com.my.package")
        val cmd = AndroidRunCommand()

        CommandLine(cmd).parseArgs(*params)

        assertThat(cmd.testTargets).isNotNull()
        assertThat(cmd.testTargets?.size).isEqualTo(2)
        assertThat(cmd.testTargets).isEqualTo(params.filter { it != testTargets })
    }

    @Test
    fun `useOrchestrator parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--use-orchestrator")

        assertThat(cmd.useOrchestrator).isTrue()
    }

    @Test
    fun `noUseOrchestrator parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--no-use-orchestrator")

        assertThat(cmd.noUseOrchestrator).isTrue()
    }

    @Test
    fun `autoGoogleLogin parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--auto-google-login")

        assertThat(cmd.autoGoogleLogin).isTrue()
    }

    @Test
    fun `noAutoGoogleLogin parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--no-auto-google-login")

        assertThat(cmd.noAutoGoogleLogin).isTrue()
    }

    @Test
    fun `performanceMetrics parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--performance-metrics")

        assertThat(cmd.performanceMetrics).isTrue()
    }

    @Test
    fun `noPerformanceMetrics parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--no-performance-metrics")

        assertThat(cmd.noPerformanceMetrics).isTrue()
    }

    @Test
    fun `numUniformShards parse`() {
        val expected = 50
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--num-uniform-shards=$expected")

        assertThat(cmd.numUniformShards).isEqualTo(expected)
    }

    @Test
    fun `testRunnerClass parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--test-runner-class=com.foo.bar.TestRunner")

        assertThat(cmd.testRunnerClass).isEqualTo("com.foo.bar.TestRunner")
    }

    @Test
    fun `environmentVariables parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--environment-variables=a=1,b=2")

        assertThat(cmd.environmentVariables).hasSize(2)
    }

    @Test
    fun `directoriesToPull parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--directories-to-pull=a,b")

        assertThat(cmd.directoriesToPull).hasSize(2)
    }

    @Test
    fun `otherFiles parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--other-files=a=1,b=2")

        assertThat(cmd.otherFiles).hasSize(2)
    }

    @Test
    fun `device parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--device=model=shamu,version=22,locale=zh_CN,orientation=default")

        val expectedDevice = Device("shamu", "22", "zh_CN", "default")
        assertThat(cmd.device?.size).isEqualTo(1)
        assertThat(cmd.device?.first()).isEqualTo(expectedDevice)
    }

    @Test
    fun `resultsBucket parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--results-bucket=a")

        assertThat(cmd.resultsBucket).isEqualTo("a")
    }

    @Test
    fun `recordVideo parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--record-video")

        assertThat(cmd.recordVideo).isTrue()
    }

    @Test
    fun `noRecordVideo parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--no-record-video")

        assertThat(cmd.noRecordVideo).isTrue()
    }

    @Test
    fun `timeout parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--timeout=1m")

        assertThat(cmd.timeout).isEqualTo("1m")
    }

    @Test
    fun `async parse`() {
        val cmd = AndroidRunCommand()
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
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--project=a")

        assertThat(cmd.project).isEqualTo("a")
    }

    @Test
    fun `resultsHistoryName parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--results-history-name=a")

        assertThat(cmd.resultsHistoryName).isEqualTo("a")
    }

    // flankYml

    @Test
    fun `maxTestShards parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--max-test-shards=3")

        assertThat(cmd.maxTestShards).isEqualTo(3)
    }

    @Test
    fun `repeatTests parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--num-test-runs=3")

        assertThat(cmd.repeatTests).isEqualTo(3)
    }

    @Test
    fun `testTargetsAlwaysRun parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--test-targets-always-run=a,b,c")

        assertThat(cmd.testTargetsAlwaysRun).isEqualTo(arrayListOf("a", "b", "c"))
    }

    @Test
    fun `resultsDir parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--results-dir=a")

        assertThat(cmd.resultsDir).isEqualTo("a")
    }

    @Test
    fun `filesToDownload parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--files-to-download=a,b")

        assertThat(cmd.filesToDownload).isEqualTo(arrayListOf("a", "b"))
    }

    @Test
    fun `flakyTestAttempts parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--num-flaky-test-attempts=10")

        assertThat(cmd.flakyTestAttempts).isEqualTo(10)
    }

    @Test
    fun `shardTime parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--shard-time=99")

        assertThat(cmd.shardTime).isEqualTo(99)
    }

    @Test
    fun `disableSharding parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--disable-sharding")

        assertThat(cmd.disableSharding).isEqualTo(true)
    }

    @Test
    fun `localResultsDir parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--local-result-dir=a")

        assertThat(cmd.localResultsDir).isEqualTo("a")
    }

    @Test
    fun `smartFlankDisableUpload parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--smart-flank-disable-upload=true")

        assertThat(cmd.smartFlankDisableUpload).isEqualTo(true)
    }

    @Test
    fun `smartFlankGcsPath parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--smart-flank-gcs-path=foo")

        assertThat(cmd.smartFlankGcsPath).isEqualTo("foo")
    }

    @Test
    fun `keepFilePath parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--keep-file-path=true")

        assertThat(cmd.keepFilePath).isEqualTo(true)
    }

    @Test
    fun `additionalAppTestApks parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--additional-app-test-apks=app=a,test=b")

        val expected = AppTestPair(app = "a", test = "b")
        assertThat(cmd.additionalAppTestApks).isEqualTo(listOf(expected))
    }

    @Test
    fun `dump-shards parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--dump-shards=true")

        assertThat(cmd.dumpShards).isEqualTo(true)
    }

    @Test
    fun `dryRun parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--dry")

        assertThat(cmd.dryRun).isEqualTo(true)
    }

    @Test
    fun `run-timeout parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--run-timeout=20s")

        assertThat(cmd.runTimeout).isEqualTo("20s")
    }
}
