package ftl.cli.firebase.test.networkprofiles

import com.google.api.services.testing.model.NetworkConfiguration
import com.google.api.services.testing.model.TrafficRule
import ftl.environment.asPrintableTable
import ftl.gc.GcTesting
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
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
            "ftl.run.common.PrettyPrintKt",
            "ftl.environment.ListNetworkConfigurationKt"
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun run() {
        mockkObject(GcTesting) {
            val configurationsMock = listOf(NetworkConfiguration().apply {
                id = "test"
                upRule = TrafficRule()
                downRule = TrafficRule()
            })
            every {
                GcTesting.networkConfiguration()
            } returns configurationsMock
            every { configurationsMock.asPrintableTable() } returns ""
            CommandLine(NetworkProfilesListCommand()).execute()
            verify { configurationsMock.asPrintableTable() }
        }
    }
}
