package ftl.analytics

import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.args.IosArgs
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
    fun `should filter default args for android`() {
        val default = AndroidArgs.default()
        val args = default.copy(
            appApk = "test"
        ).objectToMap()

        val nonDefaultArgs = args.filterNonCommonArgs().getNonDefaultArgs(default.objectToMap())

        assertThat(nonDefaultArgs).containsKey("appApk")
        assertThat(nonDefaultArgs.count()).isEqualTo(1)
    }

    @Test
    fun `should filter default args for ios`() {
        val default = IosArgs.default()
        val args = default.copy(xctestrunFile = "test").objectToMap()

        val nonDefaultArgs = args.filterNonCommonArgs().getNonDefaultArgs(default.objectToMap())

        assertThat(nonDefaultArgs).containsKey("xctestrunFile")
        assertThat(nonDefaultArgs.count()).isEqualTo(1)
    }

    @Test
    fun `should filter default args for ios with commonArgs`() {
        val default = IosArgs.default()
        val args =
            default.copy(xctestrunFile = "test", commonArgs = default.commonArgs.copy(resultsBucket = "test_bucket"))

        val nonDefaultArgs = args.createEventMap(default)

        assertThat(nonDefaultArgs).containsKey("xctestrunFile")
        assertThat(nonDefaultArgs).containsKey("resultsBucket")
        assertThat(nonDefaultArgs.count()).isEqualTo(2)
    }

    @Test
    fun `should filter default args for android with commonArgs`() {
        val default = AndroidArgs.default()
        val args = default.copy(testApk = "test", commonArgs = default.commonArgs.copy(resultsBucket = "test_bucket"))

        val nonDefaultArgs = args.createEventMap(default)

        assertThat(nonDefaultArgs).containsKey("testApk")
        assertThat(nonDefaultArgs).containsKey("resultsBucket")
        assertThat(nonDefaultArgs.count()).isEqualTo(2)
    }

    @Test
    fun `should anonymize maps to (key,anonymizedValue)`() {
        val default = AndroidArgs.default()
        val args = default.copy(environmentVariables = mapOf("testKey" to "testValue", "testKey2" to "testValue2"))

        val nonDefaultArgs = args.createEventMap(default)
        (nonDefaultArgs["environmentVariables"] as? Map<*, *>)?.let { environmentVariables ->
            assertThat(environmentVariables.count()).isEqualTo(2)
            assertThat(environmentVariables.values.all { it == "..." }).isTrue()
        }

        assertThat(nonDefaultArgs.count()).isEqualTo(1)
        assertThat(nonDefaultArgs.keys).containsExactly("environmentVariables")
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
