package ftl.cli.firebase.test.android.configuration

import ftl.android.AndroidCatalog
import ftl.util.FlankConfigurationError
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import picocli.CommandLine

class AndroidLocalesDescribeCommandTest {
    @get:Rule
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun `should execute AndroidCatalog getLocaleDescription when run AndroidLocalesDescribeCommand`() {
        mockkObject(AndroidCatalog) {
            CommandLine(AndroidLocalesDescribeCommand()).execute("pl")
            verify { AndroidCatalog.getLocaleDescription(any(), any()) }
        }
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw if locale not specified`() {
        systemOutRule.clearLog()
        CommandLine(AndroidLocalesDescribeCommand()).execute()
    }

    @Test
    fun `should return error message if locale not exists`() {
        systemOutRule.clearLog()
        CommandLine(AndroidLocalesDescribeCommand()).execute("test")
        val result = systemOutRule.log.trim()
        assertEquals("ERROR: test is not a valid locale", result)
    }
}
