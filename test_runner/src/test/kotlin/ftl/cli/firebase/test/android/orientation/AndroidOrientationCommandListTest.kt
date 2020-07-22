package ftl.cli.firebase.test.android.orientation

import com.google.common.truth.Truth
import ftl.android.AndroidCatalog
import ftl.cli.firebase.test.android.orientations.AndroidOrientationsListCommand
import ftl.cli.firebase.test.android.versions.AndroidVersionsListCommand
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Test
import picocli.CommandLine

class AndroidOrientationCommandListTest {
    @Test
    fun `should execute ListOrientations AndroidCatalog supportedOrientationsAsTable when run AndroidOrientationsListCommand`() {
        mockkObject(AndroidCatalog) {
            CommandLine(AndroidOrientationsListCommand()).execute()
            verify { AndroidCatalog.supportedOrientationsAsTable(any()) }
        }
    }

    @Test
    fun `AndroidOrientationCommandListTest should parse config`() {
        val cmd = AndroidVersionsListCommand()
        CommandLine(cmd).parseArgs("--config=a")

        Truth.assertThat(cmd.configPath).isEqualTo("a")
    }
}
