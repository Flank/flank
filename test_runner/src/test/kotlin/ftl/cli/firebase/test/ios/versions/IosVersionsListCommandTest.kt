package ftl.cli.firebase.test.ios.versions

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import picocli.CommandLine

class IosVersionsListCommandTest {

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun iosVersionsListCommandShouldParseConfig() {
        val cmd = IosVersionsListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertThat(cmd.configPath).isEqualTo("a")
    }

    @Test
    fun `should not print version information`() {
        IosVersionsListCommand().run()
        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).doesNotContainMatch("version: .*")
    }
}
