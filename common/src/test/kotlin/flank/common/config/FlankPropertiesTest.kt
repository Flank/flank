package flank.common.config

import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class FlankPropertiesTest {

    @Test
    fun `should return true for isTest`() {
        assertThat(isTest()).isTrue()
    }
}
