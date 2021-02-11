package ftl.cli.firebase.test.android

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import picocli.CommandLine

class AndroidTestEnvironmentCommandTest {

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun androidTestEnvironmentCommandShouldParseConfig() {
        val cmd = AndroidTestEnvironmentCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertThat(cmd.configPath).isEqualTo("a")
    }

    @Test
    fun `should not print version information`() {
        CommandLine(AndroidTestEnvironmentCommand()).parseArgs("--config=a")
        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).doesNotContainMatch("version: .*")
    }
}
