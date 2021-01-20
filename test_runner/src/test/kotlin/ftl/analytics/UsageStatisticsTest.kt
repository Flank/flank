package ftl.analytics

import com.google.common.truth.Truth
import ftl.args.AndroidArgs
import ftl.args.IosArgs
import ftl.util.readVersion
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.json.JSONObject
import org.junit.After
import org.junit.Test

class UsageStatisticsTest {

    @After
    fun afterTest() {
        unmockkAll()
    }

    @Test
    fun `should filter default args for android`() {
        val default = AndroidArgs.default()
        val args = default.copy(appApk = "test").objectToMap()

        val nonDefaultArgs = args.filterNonCommonArgs().getNonDefaultArgs(default.objectToMap())

        Truth.assertThat(nonDefaultArgs).containsKey("appApk")
        Truth.assertThat(nonDefaultArgs.count()).isEqualTo(1)
    }

    @Test
    fun `should filter default args for ios`() {
        val default = IosArgs.default()
        val args = default.copy(xctestrunFile = "test").objectToMap()

        val nonDefaultArgs = args.filterNonCommonArgs().getNonDefaultArgs(default.objectToMap())

        Truth.assertThat(nonDefaultArgs).containsKey("xctestrunFile")
        Truth.assertThat(nonDefaultArgs.count()).isEqualTo(1)
    }

    @Test
    fun `should filter default args for ios with commonArgs`() {
        val default = IosArgs.default()
        val args =
            default.copy(xctestrunFile = "test", commonArgs = default.commonArgs.copy(resultsBucket = "test_bucket"))

        val nonDefaultArgs = args.createEventMap(default)

        Truth.assertThat(nonDefaultArgs).containsKey("xctestrunFile")
        Truth.assertThat(nonDefaultArgs).containsKey("resultsBucket")
        Truth.assertThat(nonDefaultArgs.count()).isEqualTo(2)
    }

    @Test
    fun `should filter default args for android with commonArgs`() {
        val default = AndroidArgs.default()
        val args = default.copy(testApk = "test", commonArgs = default.commonArgs.copy(resultsBucket = "test_bucket"))

        val nonDefaultArgs = args.createEventMap(default)

        Truth.assertThat(nonDefaultArgs).containsKey("testApk")
        Truth.assertThat(nonDefaultArgs).containsKey("resultsBucket")
        Truth.assertThat(nonDefaultArgs.count()).isEqualTo(2)
    }

    @Test
    fun `should anonymize maps to (key,anonymizedValue)`() {
        val default = AndroidArgs.default()
        val args = default.copy(environmentVariables = mapOf("testKey" to "testValue", "testKey2" to "testValue2"))

        val nonDefaultArgs = args.createEventMap(default)
        (nonDefaultArgs["environmentVariables"] as? Map<*, *>)?.let { environmentVariables ->
            Truth.assertThat(environmentVariables.count()).isEqualTo(2)
            Truth.assertThat(environmentVariables.values.all { it == "..." }).isTrue()
        }

        Truth.assertThat(nonDefaultArgs.count()).isEqualTo(1)
        Truth.assertThat(nonDefaultArgs.keys).containsExactly("environmentVariables")
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
