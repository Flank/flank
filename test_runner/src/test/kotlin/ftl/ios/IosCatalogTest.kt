package ftl.ios

import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class IosCatalogTest {

    @Test
    fun supportedDevice() {
        assertThat(IosCatalog.supportedDevice("bogus", "11.2")).isFalse()
        assertThat(IosCatalog.supportedDevice("iphone8", "bogus")).isFalse()
        assertThat(IosCatalog.supportedDevice("iphone8", "11.2")).isTrue()
    }

    @Test
    fun supportedXcode() {
        assertThat(IosCatalog.supportedXcode("9.2")).isTrue()
        assertThat(IosCatalog.supportedXcode("0.1")).isFalse()
    }
}
