package ftl.cli.firebase.test.ios.versions

import ftl.ios.IosCatalog
import ftl.presentation.cli.firebase.test.ios.versions.IosVersionsDescribeCommand
import ftl.run.exception.FlankConfigurationError
import ftl.test.util.TestHelper.getThrowable
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import picocli.CommandLine

class IosVersionsDescribeCommandTest {
    @get:Rule
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun `should execute IosCatalog describeSoftwareVersion when run IosVersionsDescribeCommand`() {
        mockkObject(IosCatalog) {
            CommandLine(IosVersionsDescribeCommand()).execute("10.3")
            verify { IosCatalog.describeSoftwareVersion(any(), any()) }
        }
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw if version not specified`() {
        CommandLine(IosVersionsDescribeCommand()).execute()
    }

    @Test
    fun `should return error message if version not exists`() {
        val exception = getThrowable { CommandLine(IosVersionsDescribeCommand()).execute("test") }
        assertEquals("ERROR: 'test' is not a valid OS version", exception.message)
    }
}
