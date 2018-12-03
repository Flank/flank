package ftl.cli.firebase.test.android

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import ftl.config.Device
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
        Truth.assertThat(output).startsWith("Run tests on Firebase Test Lab")
        Truth.assertThat(output).contains("run [-h]")

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
    fun empty_params_parse_null() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parse()
        assertThat(cmd.app).isNull()
        assertThat(cmd.test).isNull()
        assertThat(cmd.testTargets).isNull()
        assertThat(cmd.useOrchestrator).isNull()
        assertThat(cmd.noUseOrchestrator).isNull()
        assertThat(cmd.autoGoogleLogin).isNull()
        assertThat(cmd.noUseOrchestrator).isNull()
        assertThat(cmd.performanceMetrics).isNull()
        assertThat(cmd.noPerformanceMetrics).isNull()
        assertThat(cmd.environmentVariables).isNull()
        assertThat(cmd.directoriesToPull).isNull()
        assertThat(cmd.device).isNull()
        assertThat(cmd.resultsBucket).isNull()
    }

    @Test
    fun app_parse() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parse("--app", "myApp.apk")
        assertThat(cmd.app).isEqualTo("myApp.apk")
    }

    @Test
    fun test_parse() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parse("--test", "myTestApp.apk")
        assertThat(cmd.test).isEqualTo("myTestApp.apk")
    }

    @Test
    fun testTargets_parse() {
        val testTargets = "--test-targets"
        val params = arrayOf(testTargets, "class com.foo.Clazz", testTargets, "package com.my.package")
        val cmd = AndroidRunCommand()

        CommandLine(cmd).parse(*params)

        assertThat(cmd.testTargets).isNotNull()
        assertThat(cmd.testTargets?.size).isEqualTo(2)
        assertThat(cmd.testTargets).isEqualTo(params.filter { it != testTargets })
    }

    @Test
    fun useOrchestrator_parse() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parse("--use-orchestrator")

        assertThat(cmd.useOrchestrator).isTrue()
    }

    @Test
    fun noUseOrchestrator_parse() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parse("--no-use-orchestrator")

        assertThat(cmd.noUseOrchestrator).isTrue()
    }

    @Test
    fun autoGoogleLogin_parse() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parse("--auto-google-login")

        assertThat(cmd.autoGoogleLogin).isTrue()
    }

    @Test
    fun noAutoGoogleLogin_parse() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parse("--no-auto-google-login")

        assertThat(cmd.noAutoGoogleLogin).isTrue()
    }

    @Test
    fun performanceMetrics_parse() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parse("--performance-metrics")

        assertThat(cmd.performanceMetrics).isTrue()
    }

    @Test
    fun noPerformanceMetrics_parse() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parse("--no-performance-metrics")

        assertThat(cmd.noPerformanceMetrics).isTrue()
    }

    @Test
    fun environmentVariables_parse() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parse("--environment-variables=a=1,b=2")

        assertThat(cmd.environmentVariables).hasSize(2)
    }

    @Test
    fun directoriesToPull_parse() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parse("--directories-to-pull=a,b")

        assertThat(cmd.directoriesToPull).hasSize(2)
    }

    @Test
    fun device_parse() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parse("--device=model=shamu,version=22,locale=zh_CN,orientation=default")

        val expectedDevice = Device("shamu", "22", "zh_CN", "default")
        assertThat(cmd.device?.size).isEqualTo(1)
        assertThat(cmd.device?.first()).isEqualTo(expectedDevice)
    }

    @Test
    fun resultsBucket_parse() {
        val cmd = AndroidRunCommand()
        CommandLine(cmd).parse("--results-bucket=a")

        assertThat(cmd.resultsBucket).isEqualTo("a")
    }
}
