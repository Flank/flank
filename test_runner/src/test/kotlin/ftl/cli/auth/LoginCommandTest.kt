package ftl.cli.auth

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import ftl.presentation.cli.auth.LoginCommand
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import picocli.CommandLine

@RunWith(FlankTestRunner::class)
class LoginCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @get:Rule
    val exit = ExpectedSystemExit.none()

    @Test
    fun cancelCommandPrintsHelp() {
        val command = LoginCommand()
        assertThat(command.usageHelpRequested).isFalse()
        CommandLine(command).execute("-h")

        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).startsWith(
            """Obtains access credentials for your user account via a web-based authorization
flow.

login [-h]
            """.trimIndent()
        )

        assertThat(command.usageHelpRequested).isTrue()
    }

    @Test
    fun commandRuns() {
        LoginCommand().run()
    }

    @Test
    fun cancelCommandOptions() {
        val cmd = LoginCommand()
        assertThat(cmd.usageHelpRequested).isFalse()
        cmd.usageHelpRequested = true
        assertThat(cmd.usageHelpRequested).isTrue()
    }
}
