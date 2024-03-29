package ftl.api

import com.google.api.services.testing.model.ProvidedSoftwareCatalog
import ftl.adapter.GoogleProvidedSoftwareCatalog

val fetchSoftwareCatalog: SoftwareCatalog.Fetch get() = GoogleProvidedSoftwareCatalog

data class SoftwareCatalog(
    val orchestratorVersion: String
) {
    interface Fetch : () -> ProvidedSoftwareCatalog
}
