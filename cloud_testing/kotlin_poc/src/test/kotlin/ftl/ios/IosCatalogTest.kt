package ftl.ios

import org.junit.Assert.assertEquals
import org.junit.Test

class IosCatalogTest {

    // iOS API requires authorization. Bitrise job is run without credentials for security reasons.
    // Skip tests that require auth when running on bitrise.
    private val bitrise = System.getenv("BITRISE_IO") != null

    @Test
    fun supported() {
        if (bitrise) return

        assertEquals(false, IosCatalog.supported("bogus", "11.2"))
        assertEquals(false, IosCatalog.supported("iphone8", "bogus"))
        assertEquals(true, IosCatalog.supported("iphone8", "11.2"))
    }
}
