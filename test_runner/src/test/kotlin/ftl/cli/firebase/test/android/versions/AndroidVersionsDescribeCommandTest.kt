package ftl.cli.firebase.test.android.versions

import ftl.android.AndroidCatalog
import ftl.util.FlankConfigurationError
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import picocli.CommandLine

class AndroidVersionsDescribeCommandTest {
    @get:Rule
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
        systemOutRule.clearLog()
        CommandLine(AndroidVersionsDescribeCommand()).execute("test")
        val result = systemOutRule.log.trim()
        assertEquals("ERROR: 'test' is not a valid OS version", result)
    }
}
