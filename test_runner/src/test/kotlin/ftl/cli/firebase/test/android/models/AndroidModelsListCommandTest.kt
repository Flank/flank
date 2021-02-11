package ftl.cli.firebase.test.android.models

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import picocli.CommandLine

class AndroidModelsListCommandTest {

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun androidModelsListCommandShouldParseConfig() {
        val cmd = AndroidModelsListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertThat(cmd.configPath).isEqualTo("a")
    }

    @Test
    fun `should not print version information`() {
        AndroidModelsListCommand().run()
        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).doesNotContainMatch("version: .*")
    }
}
