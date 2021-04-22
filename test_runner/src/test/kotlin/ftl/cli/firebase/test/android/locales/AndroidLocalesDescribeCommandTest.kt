package ftl.cli.firebase.test.android.locales

import ftl.client.google.AndroidCatalog
import ftl.presentation.cli.firebase.test.android.locales.AndroidLocalesDescribeCommand
import ftl.run.exception.FlankConfigurationError
import ftl.test.util.TestHelper.getThrowable
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
            verify { AndroidCatalog.getLocales(any()) }
        }
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw if locale not specified`() {
        CommandLine(AndroidLocalesDescribeCommand()).execute()
    }

    @Test
    fun `should return error message if locale not exists`() {
        val exception = getThrowable { CommandLine(AndroidLocalesDescribeCommand()).execute("test") }
        assertEquals("ERROR: 'test' is not a valid locale", exception.message)
    }
}
