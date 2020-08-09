package ftl.cli.firebase.test.networkprofiles

import com.google.api.services.testing.model.NetworkConfiguration
import com.google.api.services.testing.model.TrafficRule
import ftl.environment.getNetworkConfiguration
import ftl.test.util.assertThrowsWithMessage
import ftl.test.util.runMainCommand
import ftl.util.FlankConfigurationError
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule

class NetworkProfileDescribeTest {

    @get:Rule
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Before
    fun setup() {
        mockkStatic("ftl.environment.NetworkConfigurationCatalogKt")
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `should throw the configuration error if no PROFILE_ID parameter provided`() {
        assertThrowsWithMessage(FlankConfigurationError::class, "Argument PROFILE_ID must be specified.") {
            NetworkProfilesDescribeCommand().run()
        }
    }

    @Test
    fun `should print profile description if data exists`() {
        val configs = listOf(
                NetworkConfiguration().apply {
                    downRule = makeRule(0.5f)
                    upRule = makeRule(0.8f)
                    id = "ANY"
                },
                NetworkConfiguration().apply {
                    downRule = makeRule(0.1f)
                    upRule = makeRule(0.2f)
                    id = "DIFFERENT"
                }
        )
        every { getNetworkConfiguration() } returns configs

        systemOutRule.clearLog()
        runMainCommand("network-profiles", "describe", "ANY")

        val result = systemOutRule.log.trim()
        val expected = """
            downRule:
              bandwidth: 0.5
              delay: 0.5s
              packetLossRatio: 0.5
            id: ANY
            upRule:
              bandwidth: 0.8
              delay: 0.8s
              packetLossRatio: 0.8
        """.trimIndent()

        assertEquals(expected, result)
    }

    @Test
    fun `should handle case when API answers with null for configuration request`() {
        every { getNetworkConfiguration() } returns emptyList()

        systemOutRule.clearLog()
        runMainCommand("network-profiles", "describe", "NON-EXISTING")

        val result = systemOutRule.log.trim()
        val expected = "Unable to fetch profile [NON-EXISTING] description"

        assertEquals(expected, result)
    }

    @Test
    fun `should print message if unable to find provided profile`() {
        val configs = listOf(
                NetworkConfiguration().apply {
                    downRule = makeRule(0.456f)
                    id = "ANY_1"
                    upRule = makeRule(0.111f)
                },
                NetworkConfiguration().apply {
                    downRule = makeRule(0.0976f)
                    id = "ANY_2"
                    upRule = makeRule(0.234f)
                },
                NetworkConfiguration().apply {
                    downRule = makeRule(0.1f)
                    id = "ANY_3"
                    upRule = makeRule(0.11233f)
                }
        )
        every { getNetworkConfiguration() } returns configs

        systemOutRule.clearLog()
        runMainCommand("network-profiles", "describe", "NON-EXISTING")

        val result = systemOutRule.log.trim()
        val expected = "Unable to fetch profile [NON-EXISTING] description"

        assertEquals(expected, result)
    }

    @Test
    fun `should handle possible null values`() {
        val configs = listOf(
                NetworkConfiguration().apply {
                    downRule = makeRule(0.456f)
                    id = "WITH_NULLS"
                    upRule = TrafficRule().apply { packetLossRatio = 0.123f }
                }
        )
        every { getNetworkConfiguration() } returns configs

        systemOutRule.clearLog()
        runMainCommand("network-profiles", "describe", "WITH_NULLS")

        val result = systemOutRule.log.trim()
        val expected = """
            downRule:
              bandwidth: 0.456
              delay: 0.456s
              packetLossRatio: 0.456
            id: WITH_NULLS
            upRule:
              bandwidth: [Unable to fetch]
              delay: [Unable to fetch]
              packetLossRatio: 0.123
        """.trimIndent()

        assertEquals(expected, result)
    }
}

private fun makeRule(ratio: Float) = TrafficRule().apply {
    bandwidth = ratio
    delay = "${ratio}s"
    packetLossRatio = ratio
}
