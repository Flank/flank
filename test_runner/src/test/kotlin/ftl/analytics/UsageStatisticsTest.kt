package ftl.analytics

import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.test.util.FlankTestRunner
import io.mockk.unmockkAll
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
}
