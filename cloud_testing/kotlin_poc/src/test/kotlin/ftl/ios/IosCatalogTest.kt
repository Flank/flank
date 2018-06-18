package ftl.ios

import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class IosCatalogTest {

    @get:Rule
    var thrown = ExpectedException.none()!!

    // iOS API requires authorization. Bitrise job is run without credentials for security reasons.
    // Skip tests that require auth when running on bitrise.
    private val bitrise = System.getenv("BITRISE_IO") != null

    @Test
    fun validatesModelAndVersion() {
        if (bitrise) return

        IosCatalog.model("iphone8")
        IosCatalog.version("11.2")
    }

    @Test
    fun rejectsInvalidModel() {
        if (bitrise) return

        thrown.expect(RuntimeException::class.java)
        thrown.expectMessage("Invalid iOS modelId")

        IosCatalog.model("iphone88")
    }

    @Test
    fun rejectsInvalidVersion() {
        if (bitrise) return

        thrown.expect(RuntimeException::class.java)
        thrown.expectMessage("Invalid iOS versionId")

        IosCatalog.version("11.22")
    }

    @Test
    fun validatesSupportedModelAndVersion() {
        if (bitrise) return

        assertEquals(false, IosCatalog.supported("bogus", "11.2"))
        assertEquals(false, IosCatalog.supported("iphone8", "bogus"))
        assertEquals(true, IosCatalog.supported("iphone8", "11.2"))
    }
}
