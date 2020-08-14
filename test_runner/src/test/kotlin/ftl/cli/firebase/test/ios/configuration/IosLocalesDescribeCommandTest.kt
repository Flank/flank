package ftl.cli.firebase.test.ios.configuration

import ftl.ios.IosCatalog
import ftl.test.util.TestHelper
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

    private val simpleFlankPath = TestHelper.getPath("src/test/kotlin/ftl/fixtures/simple-ios-flank.yml")

    @Test
    fun `should execute IosCatalog getLocaleDescription when run IosLocalesDescribeCommand`() {
        mockkObject(IosCatalog) {
            CommandLine(IosLocalesDescribeCommand()).execute("pl", "--config=$simpleFlankPath")
            verify { IosCatalog.getLocaleDescription(any(), any()) }
        }
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw if locale not specified`() {
        systemOutRule.clearLog()
        CommandLine(IosLocalesDescribeCommand()).execute("--config=$simpleFlankPath")
    }

    @Test
    fun `should return error message if locale not exists`() {
        systemOutRule.clearLog()
        CommandLine(IosLocalesDescribeCommand()).execute("test", "--config=$simpleFlankPath")
        val result = systemOutRule.log.trim()
        assertEquals("ERROR: test is not a valid locale", result)
    }
}
