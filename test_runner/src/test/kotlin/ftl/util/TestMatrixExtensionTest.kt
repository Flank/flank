package ftl.util

import com.google.testing.model.TestMatrix
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TestMatrixExtensionTest {

    @Test
    fun testNull() {
        assertThat(TestMatrix().webLink()).isEmpty()
    }
}
