package ftl.ios

import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class IosCatalogTest {

    @get:Rule
    var thrown = ExpectedException.none()!!

    @Test
    fun validatesModelAndVersion() {
        IosCatalog.model("iphone8")
        IosCatalog.version("11.2")
    }

    @Test
    fun rejectsInvalidModel() {
        thrown.expect(RuntimeException::class.java)
        thrown.expectMessage("Invalid iOS modelId")

        IosCatalog.model("iphone88")
    }

    @Test
    fun rejectsInvalidVersion() {
        thrown.expect(RuntimeException::class.java)
        thrown.expectMessage("Invalid iOS versionId")

        IosCatalog.version("11.22")
    }
}
