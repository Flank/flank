package ftl.cli.firebase.test.android.orientations

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import ftl.android.AndroidCatalog
import ftl.cli.firebase.test.android.versions.AndroidVersionsListCommand
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import picocli.CommandLine

class AndroidOrientationCommandListTest {

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun `should execute AndroidCatalog supportedOrientationsAsTable when run AndroidOrientationsListCommand`() {
        mockkObject(AndroidCatalog) {
            CommandLine(AndroidOrientationsListCommand()).execute()
            verify { AndroidCatalog.supportedOrientationsAsTable(any()) }
        }
    }

    @Test
    fun `AndroidOrientationCommandListTest should parse config`() {
        val cmd = AndroidVersionsListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertEquals(cmd.configPath, "a")
    }

    @Test
    fun `should not print version information`() {
        AndroidOrientationsListCommand().run()
        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).doesNotContainMatch("version: .*")
    }
}
