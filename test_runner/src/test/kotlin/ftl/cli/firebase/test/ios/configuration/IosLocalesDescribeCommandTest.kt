package ftl.cli.firebase.test.ios.configuration

import ftl.ios.IosCatalog
import ftl.util.FlankConfigurationError
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import picocli.CommandLine

class IosLocalesDescribeCommandTest {
    @get:Rule
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun `should execute IosCatalog findLocale when run IosLocalesDescribeCommand`() {
        mockkObject(IosCatalog) {
            CommandLine(IosLocalesDescribeCommand()).execute("pl")
            verify { IosCatalog.getLocaleDescription(any(), any()) }
        }
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw if locale not specified`() {
        systemOutRule.clearLog()
        CommandLine(IosLocalesDescribeCommand()).execute()
    }

    @Test
    fun `should return error message if locale not exists`() {
        systemOutRule.clearLog()
        CommandLine(IosLocalesDescribeCommand()).execute("test")
        val result = systemOutRule.log.trim()
        assertEquals("ERROR: test is not a valid locale", result)
    }
}
