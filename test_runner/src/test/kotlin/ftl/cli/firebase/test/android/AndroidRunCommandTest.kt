package ftl.cli.firebase.test.android

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import ftl.args.AndroidArgs
import ftl.args.yml.AppTestPair
import ftl.client.google.GcStorage
import ftl.config.Device
import ftl.config.FtlConstants
import ftl.presentation.cli.firebase.test.android.AndroidRunCommand
import ftl.run.ANDROID_SHARD_FILE
import ftl.run.exception.FlankConfigurationError
import ftl.run.platform.android.createAndroidTestContexts
import ftl.run.saveShardChunks
import ftl.test.util.FlankTestRunner
import io.mockk.coVerify
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Assert.assertEquals
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
        assertThat(cmd.obfuscate).isFalse()
        assertThat(cmd.dryRun).isFalse()
        assertThat(cmd.config.platform.gcloud.app).isNull()
        assertThat(cmd.config.platform.gcloud.test).isNull()
        assertThat(cmd.config.platform.gcloud.additionalApks).isNull()
        assertThat(cmd.config.platform.gcloud.testTargets).isNull()
        assertThat(cmd.config.platform.gcloud.useOrchestrator).isNull()
        assertThat(cmd.config.platform.gcloud.autoGoogleLogin).isNull()
        assertThat(cmd.config.platform.gcloud.performanceMetrics).isNull()
        assertThat(cmd.config.platform.gcloud.numUniformShards).isNull()
        assertThat(cmd.config.platform.gcloud.testRunnerClass).isNull()
        assertThat(cmd.config.platform.gcloud.environmentVariables).isNull()
        assertThat(cmd.config.common.gcloud.otherFiles).isNull()
        assertThat(cmd.config.common.gcloud.devices).isNull()
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
        assertThat(cmd.config.common.flank.filesToDownload).isNull()
        assertThat(cmd.config.common.gcloud.resultsDir).isNull()
        assertThat(cmd.config.common.gcloud.flakyTestAttempts).isNull()
        assertThat(cmd.config.common.gcloud.directoriesToPull).isNull()
        assertThat(cmd.config.common.flank.disableSharding).isNull()
        assertThat(cmd.config.common.flank.localResultsDir).isNull()
        assertThat(cmd.config.common.flank.smartFlankDisableUpload).isNull()
        assertThat(cmd.config.common.flank.smartFlankGcsPath).isNull()
        assertThat(cmd.config.platform.flank.additionalAppTestApks).isNull()
        assertThat(cmd.config.common.flank.keepFilePath).isNull()
        assertThat(cmd.config.common.flank.runTimeout).isNull()
    }

    @Test
    fun `app parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--app", "myApp.apk")
        assertThat(cmd.config.platform.gcloud.app).isEqualTo("myApp.apk")
    }

    @Test
    fun `test parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--test", "myTestApp.apk")
        assertThat(cmd.config.platform.gcloud.test).isEqualTo("myTestApp.apk")
    }

    @Test
    fun `additionalApks parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--additional-apks=a.apk,b.apk")
        assertThat(cmd.config.platform.gcloud.additionalApks).isEqualTo(listOf("a.apk", "b.apk"))
    }

    @Test
    fun `testTargets parse`() {
        val testTargets = "--test-targets"
        val params = arrayOf(testTargets, "class com.foo.Clazz", testTargets, "package com.my.package")
        val cmd = AndroidRunCommand()

        CommandLine(cmd).parseArgs(*params)

        assertThat(cmd.config.platform.gcloud.testTargets).isNotNull()
        assertThat(cmd.config.platform.gcloud.testTargets?.size).isEqualTo(2)
        assertThat(cmd.config.platform.gcloud.testTargets).isEqualTo(params.filter { it != testTargets })
    }

    @Test
    fun `useOrchestrator parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--use-orchestrator")

        assertThat(cmd.config.platform.gcloud.useOrchestrator).isTrue()
    }

    @Test
    fun `noUseOrchestrator parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--no-use-orchestrator")

        assertThat(cmd.config.platform.gcloud.useOrchestrator).isFalse()
    }

    @Test
    fun `autoGoogleLogin parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--auto-google-login")

        assertThat(cmd.config.platform.gcloud.autoGoogleLogin).isTrue()
    }

    @Test
    fun `noAutoGoogleLogin parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--no-auto-google-login")

        assertThat(cmd.config.platform.gcloud.autoGoogleLogin).isFalse()
    }

    @Test
    fun `performanceMetrics parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--performance-metrics")

        assertThat(cmd.config.platform.gcloud.performanceMetrics).isTrue()
    }

    @Test
    fun `noPerformanceMetrics parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--no-performance-metrics")

        assertThat(cmd.config.platform.gcloud.performanceMetrics).isFalse()
    }

    @Test
    fun `numUniformShards parse`() {
        val expected = 50
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--num-uniform-shards=$expected")

        assertThat(cmd.config.platform.gcloud.numUniformShards).isEqualTo(expected)
    }

    @Test
    fun `testRunnerClass parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--test-runner-class=com.foo.bar.TestRunner")

        assertThat(cmd.config.platform.gcloud.testRunnerClass).isEqualTo("com.foo.bar.TestRunner")
    }

    @Test
    fun `environmentVariables parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--environment-variables=a=1,b=2")

        assertThat(cmd.config.platform.gcloud.environmentVariables).hasSize(2)
    }

    @Test
    fun `directoriesToPull parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--directories-to-pull=a,b")

        assertThat(cmd.config.common.gcloud.directoriesToPull).hasSize(2)
    }

    @Test
    fun `otherFiles parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--other-files=a=1,b=2")

        assertThat(cmd.config.common.gcloud.otherFiles).hasSize(2)
    }

    @Test
    fun `device parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--device=model=shamu,version=22,locale=zh_CN,orientation=default")

        val expectedDevice = Device("shamu", "22", "zh_CN", "default")
        assertThat(cmd.config.common.gcloud.devices?.size).isEqualTo(1)
        assertThat(cmd.config.common.gcloud.devices?.first()).isEqualTo(expectedDevice)
    }

    @Test
    fun `resultsBucket parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--results-bucket=a")

        assertThat(cmd.config.common.gcloud.resultsBucket).isEqualTo("a")
    }

    @Test
    fun `recordVideo parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--record-video")

        assertThat(cmd.config.common.gcloud.recordVideo).isTrue()
    }

    @Test
    fun `noRecordVideo parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--no-record-video")

        assertThat(cmd.config.common.gcloud.recordVideo).isFalse()
    }

    @Test
    fun `timeout parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--timeout=1m")

        assertThat(cmd.config.common.gcloud.timeout).isEqualTo("1m")
    }

    @Test
    fun `async parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--async")

        assertThat(cmd.config.common.gcloud.async).isTrue()
    }

    @Test
    fun `clientDetails parse`() {
        val cmd = AndroidRunCommand()
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
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--network-profile=a")

        assertThat(cmd.config.common.gcloud.networkProfile).isEqualTo("a")
    }

    @Test
    fun `project parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--project=a")

        assertThat(cmd.config.common.flank.project).isEqualTo("a")
    }

    @Test
    fun `resultsHistoryName parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--results-history-name=a")

        assertThat(cmd.config.common.gcloud.resultsHistoryName).isEqualTo("a")
    }

    // flankYml

    @Test
    fun `maxTestShards parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--max-test-shards=3")

        assertThat(cmd.config.common.flank.maxTestShards).isEqualTo(3)
    }

    @Test
    fun `repeatTests parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--num-test-runs=3")

        assertThat(cmd.config.common.flank.repeatTests).isEqualTo(3)
    }

    @Test
    fun `testTargetsAlwaysRun parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--test-targets-always-run=a,b,c")

        assertThat(cmd.config.common.flank.testTargetsAlwaysRun).isEqualTo(arrayListOf("a", "b", "c"))
    }

    @Test
    fun `resultsDir parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--results-dir=a")

        assertThat(cmd.config.common.gcloud.resultsDir).isEqualTo("a")
    }

    @Test
    fun `filesToDownload parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--files-to-download=a,b")

        assertThat(cmd.config.common.flank.filesToDownload).isEqualTo(arrayListOf("a", "b"))
    }

    @Test
    fun `flakyTestAttempts parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--num-flaky-test-attempts=10")

        assertThat(cmd.config.common.gcloud.flakyTestAttempts).isEqualTo(10)
    }

    @Test
    fun `shardTime parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--shard-time=99")

        assertThat(cmd.config.common.flank.shardTime).isEqualTo(99)
    }

    @Test
    fun `disableSharding parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--disable-sharding")

        assertThat(cmd.config.common.flank.disableSharding).isEqualTo(true)
    }

    @Test
    fun `localResultsDir parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--local-result-dir=a")

        assertThat(cmd.config.common.flank.localResultsDir).isEqualTo("a")
    }

    @Test
    fun `smartFlankDisableUpload parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--smart-flank-disable-upload=true")

        assertThat(cmd.config.common.flank.smartFlankDisableUpload).isEqualTo(true)
    }

    @Test
    fun `smartFlankGcsPath parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--smart-flank-gcs-path=foo")

        assertThat(cmd.config.common.flank.smartFlankGcsPath).isEqualTo("foo")
    }

    @Test
    fun `keepFilePath parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--keep-file-path=true")

        assertThat(cmd.config.common.flank.keepFilePath).isEqualTo(true)
    }

    @Test
    fun `additionalAppTestApks parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--additional-app-test-apks=app=a,test=b")

        val expected = AppTestPair(app = "a", test = "b")
        assertThat(cmd.config.platform.flank.additionalAppTestApks).isEqualTo(listOf(expected))
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

        assertThat(cmd.config.common.flank.runTimeout).isEqualTo("20s")
    }

    @Test
    fun `robo-directives parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--robo-directives=text:a=b,click:c=")

        assertEquals(
            mapOf(
                "text:a" to "b",
                "click:c" to ""
            ),
            cmd.config.platform.gcloud.roboDirectives
        )
    }

    @Test
    fun `robo-script parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--robo-script=a")

        assertThat(cmd.config.platform.gcloud.roboScript).isEqualTo("a")
    }

    @Test
    fun `obfuscate parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--obfuscate")

        assertThat(cmd.obfuscate).isTrue()
    }

    @Test
    fun `default-test-time parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--default-test-time=15")

        assertThat(cmd.config.common.flank.defaultTestTime).isEqualTo(15.0)
    }

    @Test
    fun `use-average-test-time-for-new-tests parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--use-average-test-time-for-new-tests")

        assertThat(cmd.config.common.flank.useAverageTestTimeForNewTests).isTrue()
    }

    @Test
    fun `--output-report parse`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--output-report=json")

        assertThat(cmd.config.common.flank.outputReport).isEqualTo("json")
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw if --full-junit-result and JUnitResult xml used`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--full-junit-result", "--smart-flank-gcs-path=gs://test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2020-08-26_15-20-23.850738_rtGt/JUnitReport.xml")
        cmd.run()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw if FullJUnitResult xml used and --full-junit-result not set`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--smart-flank-gcs-path=gs://test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2020-08-26_15-20-23.850738_rtGt/FullJUnitReport.xml")
        cmd.run()
    }

    @Test
    fun `should not throw if --full-junit-result and smart flank path different than JUnitReport xml`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--full-junit-result", "--smart-flank-gcs-path=gs://test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2020-08-26_15-20-23.850738_rtGt/JUnitReportTest.xml")
        cmd.run()
    }

    @Test
    fun `should not throw if --full-junit-result not set and smart flank path different than JUnitReport xml`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--smart-flank-gcs-path=gs://test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2020-08-26_15-20-23.850738_rtGt/JUnitReportTest.xml")
        cmd.run()
    }

    @Test
    fun `should not validate if smart-flank-disable-upload set`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs(
            "--smart-flank-disable-upload",
            "--full-junit-result",
            "--smart-flank-gcs-path=gs://test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2020-08-26_15-20-23.850738_rtGt/JUnitReport.xml"
        )
        cmd.run()
    }

    @Test
    fun `should dump shards on android test run`() {
        mockkStatic("ftl.run.DumpShardsKt")
        val runCmd = AndroidRunCommand()
        runCmd.configPath = "./src/test/kotlin/ftl/fixtures/simple-android-flank.yml"
        runCmd.run()
        verify { saveShardChunks(any(), any(), any(), any()) }
    }

    @Test
    fun `should dump shards on android test run and not upload when disable-upload-results set`() {
        mockkStatic("ftl.run.DumpShardsKt")
        mockkObject(GcStorage) {
            val runCmd = AndroidRunCommand()
            runCmd.configPath = "./src/test/kotlin/ftl/fixtures/simple-android-flank.yml"
            CommandLine(runCmd).parseArgs("--disable-results-upload")
            runCmd.run()
            verify { saveShardChunks(any(), any(), any(), any()) }
            verify(inverse = true) { GcStorage.upload(ANDROID_SHARD_FILE, any(), any()) }
        }
    }

    @Test
    fun `should calculate shards only one time on newRun`() {
        mockkStatic("ftl.run.DumpShardsKt", "ftl.run.platform.android.CreateAndroidTestContextKt")
        mockkObject(GcStorage) {
            val runCmd = AndroidRunCommand()
            runCmd.configPath = "./src/test/kotlin/ftl/fixtures/simple-android-flank.yml"
            runCmd.run()
            coVerify(exactly = 1) { any<AndroidArgs>().createAndroidTestContexts() }
        }
    }

    @Test
    fun `should properly parse type`() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parseArgs("--type=a")

        assertThat(cmd.config.common.gcloud.type).isEqualTo("a")
    }

    @Test
    fun `should print version information`() {
        AndroidRunCommand().run()
        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).containsMatch("version: .*")
    }
}
