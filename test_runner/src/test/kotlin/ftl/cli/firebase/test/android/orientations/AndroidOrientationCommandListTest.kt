package ftl.cli.firebase.test.android.orientations

import ftl.client.google.AndroidCatalog
import ftl.presentation.cli.firebase.test.android.orientations.AndroidOrientationsListCommand
import ftl.presentation.cli.firebase.test.android.versions.AndroidVersionsListCommand
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import picocli.CommandLine

class AndroidOrientationCommandListTest {
    @Test
    fun `should execute AndroidCatalog supportedOrientationsAsTable when run AndroidOrientationsListCommand`() {
        mockkObject(AndroidCatalog) {
            CommandLine(AndroidOrientationsListCommand()).execute()
            verify { AndroidCatalog.supportedOrientations(any()) }
        }
    }

    @Test
    fun `AndroidOrientationCommandListTest should parse config`() {
        val cmd = AndroidVersionsListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        assertEquals(cmd.configPath, "a")
    }
}
