package ftl.ios

import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class IosCatalogTest {

    private val projectId = ""

    @Test
    fun supportedDevice() {
        assertThat(IosCatalog.supportedDevice("bogus", "11.2", projectId)).isFalse()
        assertThat(IosCatalog.supportedDevice("iphone8", "bogus", projectId)).isFalse()
        assertThat(IosCatalog.supportedDevice("iphone8", "11.2", projectId)).isTrue()
    }

    @Test
    fun supportedXcode() {
        assertThat(IosCatalog.supportedXcode("9.2", projectId)).isTrue()
        assertThat(IosCatalog.supportedXcode("0.1", projectId)).isFalse()
    }
}
