package ftl.cli.firebase.test.networkprofiles

import ftl.api.NetworkProfile
import ftl.api.fetchNetworkProfiles
import ftl.presentation.cli.firebase.test.networkprofiles.NetworkProfilesListCommand
import ftl.presentation.cli.firebase.test.networkprofiles.toCliTable
import io.mockk.every
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
        mockkStatic(List<NetworkProfile>::toCliTable)
        mockkStatic(::fetchNetworkProfiles)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun run() {
        every {
            fetchNetworkProfiles()
        } returns emptyList()
        every {
            any<List<NetworkProfile>>().toCliTable()
        } returns ""
        CommandLine(NetworkProfilesListCommand()).execute()
        verify { any<List<NetworkProfile>>().toCliTable() }
    }
}
