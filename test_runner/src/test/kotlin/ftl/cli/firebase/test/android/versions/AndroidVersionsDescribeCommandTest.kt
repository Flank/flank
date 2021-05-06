package ftl.cli.firebase.test.android.versions

import ftl.presentation.cli.firebase.test.android.versions.AndroidVersionsDescribeCommand
import ftl.presentation.cli.firebase.test.ios.versions.IosVersionsDescribeCommand
import ftl.run.exception.FlankConfigurationError
import ftl.test.util.TestHelper
import org.junit.Assert.assertEquals
import org.junit.Test
import picocli.CommandLine

class AndroidVersionsDescribeCommandTest {

    @Test(expected = FlankConfigurationError::class)
    fun `should throw if version not specified`() {
        CommandLine(AndroidVersionsDescribeCommand()).execute()
    }

    @Test
    fun `should return error message if version not exists`() {
        val exception = TestHelper.getThrowable { CommandLine(IosVersionsDescribeCommand()).execute("test") }
        assertEquals("ERROR: 'test' is not a valid OS version", exception.message)
    }
}
