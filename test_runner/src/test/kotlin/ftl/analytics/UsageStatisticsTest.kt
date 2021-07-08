package ftl.analytics

import com.google.common.truth.Truth.assertThat
import flank.tool.analytics.mixpanel.send
import ftl.args.AndroidArgs
import ftl.test.util.FlankTestRunner
import ftl.util.readVersion
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.json.JSONObject
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class UsageStatisticsTest {

    @After
    fun afterTest() {
        unmockkAll()
    }

    @Test
    fun `should anonymize maps to (key,anonymizedValue)`() {
        val default = AndroidArgs.default()
        val args = default.copy(environmentVariables = mapOf("testKey" to "testValue", "testKey2" to "testValue2"))

        val nonDefaultArgs = args.createEventMap()
        (nonDefaultArgs["environmentVariables"] as? Map<*, *>)?.let { environmentVariables ->
            assertThat(environmentVariables.count()).isEqualTo(2)
            assertThat(environmentVariables.values.all { it == "..." }).isTrue()
        }

        assertThat(nonDefaultArgs.keys).contains("environmentVariables")
    }

    @Test
    fun `should not run send configuration if unit tests`() {
        mockkStatic(JSONObject::send)

        AndroidArgs.default().sendConfiguration()

        verify(inverse = true) { any<JSONObject>().send() }
    }

    @Test
    fun `should not run send configuration if disable statistic param set`() {
        mockkStatic(JSONObject::send)
        mockkStatic(::readVersion)

        every { readVersion() } returns "test"

        AndroidArgs.default().run {
            copy(commonArgs = commonArgs.copy(disableUsageStatistics = true))
                .sendConfiguration()
        }

        verify(inverse = true) { any<JSONObject>().send() }
    }
}
