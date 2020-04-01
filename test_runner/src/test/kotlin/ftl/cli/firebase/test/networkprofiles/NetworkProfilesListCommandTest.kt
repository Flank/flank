package ftl.cli.firebase.test.networkprofiles

import com.google.api.services.testing.Testing
import com.google.api.services.testing.model.NetworkConfiguration
import com.google.api.services.testing.model.NetworkConfigurationCatalog
import com.google.api.services.testing.model.TestEnvironmentCatalog
import ftl.http.executeWithRetry
import ftl.run.common.prettyPrint
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import picocli.CommandLine

class NetworkProfilesListCommandTest {

    @Before
    fun setUp() {
        mockkStatic(
            "ftl.http.ExecuteWithRetryKt",
            "ftl.run.common.PrettyPrintKt"
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun run() {
        val configurationsMock = emptyList<NetworkConfiguration>()
        val prettyPrintSpy = spyk(prettyPrint)
        every { prettyPrint } returns prettyPrintSpy
        every {
            any<Testing.TestEnvironmentCatalog.Get>().executeWithRetry()
        } returns TestEnvironmentCatalog().apply {
            networkConfigurationCatalog = NetworkConfigurationCatalog().apply {
                configurations = configurationsMock
            }
        }

        CommandLine(NetworkProfilesListCommand()).execute()

        verify { prettyPrintSpy.toJson(configurationsMock) }
    }
}
