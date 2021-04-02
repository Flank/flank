package ftl.cli.firebase.test.ios.orientations

import ftl.ios.IosCatalog
import ftl.presentation.cli.firebase.test.ios.orientations.IosOrientationsListCommand
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import picocli.CommandLine

class IosOrientationCommandListTest {
    @Test
    fun `should execute IosCatalog supportedOrientationsAsTable when run IosOrientationsListCommand`() {
        mockkObject(IosCatalog) {
            CommandLine(IosOrientationsListCommand()).execute()
            verify { IosCatalog.supportedOrientationsAsTable(any()) }
        }
    }

    @Test
    fun `IosOrientationsListCommand should parse config`() {
        val cmd = IosOrientationsListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertEquals(cmd.configPath, "a")
    }
}
