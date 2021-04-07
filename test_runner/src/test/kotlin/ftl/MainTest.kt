package ftl

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import ftl.presentation.cli.MainCommand
import ftl.test.util.FlankTestRunner
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import picocli.CommandLine

@RunWith(FlankTestRunner::class)
class MainTest {

    @get:Rule
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @get:Rule
    val systemErrRule: SystemErrRule = SystemErrRule().enableLog().muteForSuccessfulTests()

    @get:Rule
    val systemExit: ExpectedSystemExit = ExpectedSystemExit.none()

    @get:Rule
    val root = TemporaryFolder()

    private fun assertMainHelpStrings(output: String) {
        assertThat(output.normalizeLineEnding()).contains(
            "flank.jar\n" +
                " [-v] [--debug] [COMMAND]\n" +
                "      --debug     Enables debug logging\n" +
                "  -v, --version   Prints the version\n" +
                "Commands:\n" +
                "  firebase\n" +
                "  ios\n" +
                "  android\n"
        )
    }

    private fun runCommand(vararg args: String): String {
        systemErrRule.clearLog()
        systemOutRule.clearLog()
        CommandLine(MainCommand()).execute(*args)
        return systemOutRule.log.normalizeLineEnding() + systemErrRule.log.normalizeLineEnding()
    }

    private fun unknownOption(option: String) = "Unknown option: '$option'"

    @Test
    fun mainCLIVersionOption() {
        val option = "-v"
        assertThat(
            runCommand(option)
        ).doesNotContain(
            unknownOption(option)
        )
    }

    @Test
    fun mainCLIDebugOption() {
        val option = "--debug"
        assertThat(
            runCommand(option)
        ).doesNotContain(
            unknownOption(option)
        )
    }

    @Test
    fun mainCLIDisplaysHelp() {
        assertMainHelpStrings(runCommand())
    }

    @Test
    fun mainCLIErrorsOnUnknownFlag() {
        val option = "-unknown-flag"
        val output = runCommand(option)
        assertThat(output).contains(unknownOption(option))
        assertMainHelpStrings(output)
    }

    @Test
    fun `should exit with status code 0 if no args provided`() {
        systemExit.expectSystemExitWithStatus(0)
        main(emptyArray())
        assertMainHelpStrings(systemOutRule.log)
    }

    @Test
    fun `should terminate jvm with exit status 2 if yml parsing error occurs`() {
        systemExit.expectSystemExitWithStatus(2)
        main(
            arrayOf(
                "firebase",
                "test",
                "android",
                "run",
                "--dry",
                "-c=./src/test/kotlin/ftl/fixtures/invalid.yml"
            )
        )
    }

    @Test
    fun `flank entrypoint should be ftl_Main`() {
        // For reference: https://github.com/Flank/flank/issues/1780

        val logFile = root.newFile()
        val result = ProcessBuilder(
            "java",
            "-cp",
            System.getProperty("java.class.path"),
            "ftl.Main"
        )
            .redirectOutput(logFile)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start().waitFor()

        assertEquals("Process did not finish with exit code 0, check entry point", 0, result)
        assertMainHelpStrings(logFile.readText())
    }
}
