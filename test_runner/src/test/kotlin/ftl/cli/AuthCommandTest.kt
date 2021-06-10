package ftl.cli

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import ftl.presentation.cli.AuthCommand
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class AuthCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun firebaseCommandPrintsHelp() {
        AuthCommand().run()
        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).startsWith(
            "Manage oauth2 credentials for Google Cloud\n\n" +
                "auth [-h] [COMMAND]\n" +
                "  -h, --help   Prints this help message\n" +
                "Commands:\n" +
                "  login"
        )
    }
}
