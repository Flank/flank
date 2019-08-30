package ftl

import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import picocli.CommandLine

@RunWith(FlankTestRunner::class)
class MainTest {

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Rule
    @JvmField
    val systemErrRule: SystemErrRule = SystemErrRule().enableLog().muteForSuccessfulTests()

    private fun assertMainHelpStrings(output: String) {
        assertThat(output.normalizeLineEnding()).contains(
            "flank.jar\n" +
                " [-v] [COMMAND]\n" +
                "  -v, --version   Prints the version\n" +
                "Commands:\n" +
                "  firebase\n" +
                "  ios\n" +
                "  android\n"
        )
    }

    private fun String.normalizeLineEnding(): String  {
        // required for tests to pass on Windows
        return this.replace("\r\n", "\n")
    }

    private fun runCommand(vararg args: String): String {
        systemErrRule.clearLog()
        systemOutRule.clearLog()
        CommandLine(Main()).execute(*args)
        return systemOutRule.log.normalizeLineEnding() + systemErrRule.log.normalizeLineEnding()
    }

    @Test
    fun mainCLIVersionCommand() {
        assertThat(
            runCommand("-v")
        ).isNotEmpty()
    }

    @Test
    fun mainCLIDisplaysHelp() {
        assertMainHelpStrings(runCommand())
    }

    @Test
    fun mainCLIErrorsOnUnknownFlag() {
        val output = runCommand("-unknown-flag")
        assertThat(output).contains("Unknown option: '-unknown-flag'")
        assertMainHelpStrings(output)
    }

    @Test
    fun mainStaticEntrypoint() {
        Main.main(emptyArray())
        assertMainHelpStrings(systemOutRule.log)
    }
}
