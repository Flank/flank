package ftl.cli.firebase.test.ios.configuration

import ftl.client.google.IosCatalog
import ftl.presentation.cli.firebase.test.ios.configuration.IosLocalesDescribeCommand
import ftl.run.exception.FlankConfigurationError
import ftl.test.util.TestHelper
import ftl.test.util.TestHelper.getThrowable
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
            verify { IosCatalog.getLocales(any()) }
        }
    }

    /*
    every { fetchLocales(any()) } returns emptyList()
        every { any<List<Locale>>().asPrintableTable() } returns ""
        CommandLine(AndroidLocalesListCommand()).execute()
        verify { any<List<Locale>>().asPrintableTable() }
    * */

    @Test(expected = FlankConfigurationError::class)
    fun `should throw if locale not specified`() {
        CommandLine(IosLocalesDescribeCommand()).execute("--config=$simpleFlankPath")
    }

    @Test
    fun `should return error message if locale not exists`() {
        val exception = getThrowable { CommandLine(IosLocalesDescribeCommand()).execute("test", "--config=$simpleFlankPath") }
        val result = exception.message
        assertEquals("ERROR: 'test' is not a valid locale", result)
    }
}
