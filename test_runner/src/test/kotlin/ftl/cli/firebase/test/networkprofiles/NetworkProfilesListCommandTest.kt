package ftl.cli.firebase.test.networkprofiles

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import ftl.environment.networkConfigurationAsTable
import ftl.gc.GcTesting
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import picocli.CommandLine

class NetworkProfilesListCommandTest {

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Before
    fun setUp() {
        mockkStatic(
            "ftl.environment.NetworkConfigurationCatalogKt"
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

    @Test
    fun `should not print version information`() {
        mockkObject(GcTesting) {
            every {
                networkConfigurationAsTable()
            } returns ""
            NetworkProfilesListCommand().run()
            val output = systemOutRule.log.normalizeLineEnding()
            assertThat(output).doesNotContainMatch("version: .*")
        }
    }
}
