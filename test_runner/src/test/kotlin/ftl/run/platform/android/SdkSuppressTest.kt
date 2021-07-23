package ftl.run.platform.android

import com.google.common.truth.Truth.assertThat
import com.linkedin.dex.parser.DecodedValue
import com.linkedin.dex.parser.TestMethod
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class SdkSuppressTest {

    @Test
    fun `should properly return that test contains sdksuppress annotations for androidx_test_filters_SdkSuppress`() {
        // given
        val testMethod: TestMethod = mockk() {
            every { annotations } returns listOf(
                mockk {
                    every { name } returns "androidx.test.filters.SdkSuppress"
                }
            )
        }

        // when
        val actual = testMethod.isSdkSuppressTest()

        // then
        assertThat(actual).isTrue()
    }

    @Test
    fun `should properly return that test contains sdksuppress annotations for android_support_test_filters_SdkSuppress`() {
        // given
        val testMethod: TestMethod = mockk {
            every { annotations } returns listOf(
                mockk {
                    every { name } returns "android.support.test.filters.SdkSuppress"
                }
            )
        }

        // when
        val actual = testMethod.isSdkSuppressTest()

        // then
        assertThat(actual).isTrue()
    }

    @Test
    fun `should properly return that test does not contain sdksuppress annotations`() {
        // given
        val testMethod: TestMethod = mockk {
            every { annotations } returns listOf(
                mockk {
                    every { name } returns "Ignore"
                },
                mockk {
                    every { name } returns "Custom"
                }
            )
        }

        // when
        val actual = testMethod.isSdkSuppressTest()

        // then
        assertThat(actual).isFalse()
    }

    @Test
    fun `should properly return min and max sdk level for suppress test`() {
        // given
        val expected = SdkSuppressLevels(22, 24)
        val testMethod: TestMethod = mockk {
            every { annotations } returns listOf(
                mockk {
                    every { name } returns "androidx.test.filters.SdkSuppress"
                    every { values } returns mapOf(
                        "minSdkVersion" to DecodedValue.DecodedInt(22),
                        "maxSdkVersion" to DecodedValue.DecodedInt(24)
                    )
                },
            )
        }

        // when
        val sdkSuppressLevels = testMethod.sdkSuppressLevels()

        // then
        assertThat(sdkSuppressLevels).isEqualTo(expected)
    }
}
