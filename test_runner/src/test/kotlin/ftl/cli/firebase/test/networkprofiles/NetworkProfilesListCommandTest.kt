package ftl.cli.firebase.test.networkprofiles

import ftl.environment.networkConfigurationAsTable
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
            "ftl.environment.common.ListNetworkConfigurationKt"
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun run() {
        mockkObject(GcTesting) {
            every {
                networkConfigurationAsTable()
            } returns ""
            CommandLine(NetworkProfilesListCommand()).execute()
            verify { networkConfigurationAsTable() }
        }
    }
}
