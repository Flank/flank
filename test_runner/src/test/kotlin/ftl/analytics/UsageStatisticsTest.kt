package ftl.analytics

import com.google.common.truth.Truth.assertThat
import flank.tool.analytics.mixpanel.Mixpanel
import flank.tool.analytics.mixpanel.toMap
import ftl.args.AndroidArgs
import ftl.args.IosArgs
import ftl.test.util.FlankTestRunner
import ftl.util.readVersion
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertTrue
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
        args.initUsageStatistics()
        val nonDefaultArgs = args.toMap()
        (nonDefaultArgs["environmentVariables"] as? Map<*, *>)?.let { environmentVariables ->
            assertThat(environmentVariables.count()).isEqualTo(2)
            assertThat(environmentVariables.values.all { it == "..." }).isTrue()
        }

        assertThat(nonDefaultArgs.keys).contains("environmentVariables")
    }

    @Test
    fun `should not run send configuration if unit tests`() {
        mockkObject(Mixpanel)

        every { Mixpanel.send(any()) }

        AndroidArgs.default().reportConfiguration()

        verify(inverse = true) { Mixpanel.send(any()) }
    }

    @Test
    fun `should not run send configuration if disable statistic param set`() {
        mockkObject(Mixpanel)
        mockkStatic(::readVersion)

        every { readVersion() } returns "test"
        every { Mixpanel.send(any()) }

        AndroidArgs.default().run {
            copy(commonArgs = commonArgs.copy(disableUsageStatistics = true))
                .reportConfiguration()
        }

        verify(inverse = true) { Mixpanel.send(any()) }
    }

    @Test
    fun `should ignore commonArgs from AndroidArgs`() {
        // given
        val args = AndroidArgs.default()

        // when
        val data = args.toMap()

        // then
        assertTrue(args::commonArgs.name !in data)
    }

    @Test
    fun `should ignore commonArgs from IosArgs`() {
        // given
        val args = IosArgs.default()

        // when
        val data = args.toMap()

        // then
        assertTrue(args::commonArgs.name !in data)
    }
}
