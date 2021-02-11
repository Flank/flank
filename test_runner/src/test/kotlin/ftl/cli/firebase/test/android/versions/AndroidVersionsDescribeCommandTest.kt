package ftl.cli.firebase.test.android.versions

import com.google.common.truth.Truth
import flank.common.normalizeLineEnding
import ftl.android.AndroidCatalog
import ftl.cli.firebase.test.ios.versions.IosVersionsDescribeCommand
import ftl.run.exception.FlankConfigurationError
import ftl.test.util.TestHelper
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import picocli.CommandLine

class AndroidVersionsDescribeCommandTest {
    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun `should execute AndroidCatalog describeSoftwareVersion when run AndroidVersionsDescribeCommand`() {
        mockkObject(AndroidCatalog) {
            CommandLine(AndroidVersionsDescribeCommand()).execute("21")
            verify { AndroidCatalog.describeSoftwareVersion(any(), any()) }
        }
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw if version not specified`() {
        CommandLine(AndroidVersionsDescribeCommand()).execute()
    }

    @Test
    fun `should return error message if version not exists`() {
        val exception = TestHelper.getThrowable { CommandLine(IosVersionsDescribeCommand()).execute("test") }
        assertEquals("ERROR: 'test' is not a valid OS version", exception.message)
    }

    @Test
    fun `should not print version information`() {
        CommandLine(AndroidVersionsDescribeCommand()).execute("21")
        val output = systemOutRule.log.normalizeLineEnding()
        Truth.assertThat(output).doesNotContainMatch("version: .*")
    }
}
