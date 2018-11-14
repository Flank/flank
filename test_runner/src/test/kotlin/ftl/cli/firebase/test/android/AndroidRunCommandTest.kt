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
        assertThat(cmd.app).isEqualTo(null)
        assertThat(cmd.test).isEqualTo(null)
        assertThat(cmd.testTargets).isEqualTo(null)
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
        val params = arrayOf(testTargets, "class com.foo.Clazz", "--test-targets", "package com.my.package")
        val cmd = AndroidRunCommand()

        CommandLine(cmd).parse(*params)

        assertThat(cmd.testTargets).isNotNull()
        assertThat(cmd.testTargets?.size).isEqualTo(2)
        assertThat(cmd.testTargets).isEqualTo(params.filter { it != testTargets })
    }
}
