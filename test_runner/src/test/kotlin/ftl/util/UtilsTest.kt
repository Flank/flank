package ftl.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UtilsTest {

    @Test(expected = RuntimeException::class)
    fun readTextResource_errors() {
        Utils.readTextResource("does not exist")
    }

    @Test
    fun readTextResource_succeeds() {
        assertThat(Utils.readTextResource("version.txt")).isNotNull()
    }
}
