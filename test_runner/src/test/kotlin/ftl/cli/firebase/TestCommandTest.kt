package ftl.cli.firebase

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import ftl.presentation.cli.firebase.TestCommand
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class TestCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun testCommandPrintsHelp() {
        TestCommand().run()
        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).startsWith(
            "test [-h] [COMMAND]\n" +
                "  -h, --help   Prints this help message\n" +
                "Commands:\n" +
                "  android\n" +
                "  ios\n"
        )
    }
}
