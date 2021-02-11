package ftl.cli.firebase.test.ios.models

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import picocli.CommandLine

class IosModelsListCommandTest {

    @get:Rule
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun iosModelsListCommandShouldParseConfig() {
        val cmd = IosModelsListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertThat(cmd.configPath).isEqualTo("a")
    }

    @Test
    fun `should not print version information`() {
        IosModelsListCommand().run()
        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).doesNotContainMatch("version: .*")
    }
}
