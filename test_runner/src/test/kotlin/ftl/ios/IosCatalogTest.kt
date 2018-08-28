package ftl.ios

import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class IosCatalogTest {

    @Test
    fun supported() {
        assertThat(IosCatalog.supported("bogus", "11.2")).isFalse()
        assertThat(IosCatalog.supported("iphone8", "bogus")).isFalse()
        assertThat(IosCatalog.supported("iphone8", "11.2")).isTrue()
    }
}
