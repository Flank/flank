package ftl.cli.firebase

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
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
        ftl.presentation.cli.firebase.TestCommand().run()
        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).startsWith(
            "test [COMMAND]\n" +
                "Commands:\n" +
                "  android\n" +
                "  ios\n"
        )
    }
}
