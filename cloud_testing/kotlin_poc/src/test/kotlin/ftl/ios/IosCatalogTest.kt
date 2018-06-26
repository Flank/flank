package ftl.ios

import ftl.config.FtlConstants
import org.junit.Assert.assertEquals
import org.junit.Test

class IosCatalogTest {

    init {
        FtlConstants.useMock = true
    }

    @Test
    fun supported() {
        assertEquals(false, IosCatalog.supported("bogus", "11.2"))
        assertEquals(false, IosCatalog.supported("iphone8", "bogus"))
        assertEquals(true, IosCatalog.supported("iphone8", "11.2"))
    }
}
