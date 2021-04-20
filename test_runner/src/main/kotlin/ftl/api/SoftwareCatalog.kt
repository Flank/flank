package ftl.api

import com.google.testing.model.ProvidedSoftwareCatalog
import ftl.adapter.GoogleProvidedSoftwareCatalog
import ftl.adapter.GoogleProvidedSoftwareCatalogAsTable

val fetchSoftwareCatalog: SoftwareCatalog.Fetch get() = GoogleProvidedSoftwareCatalog
val fetchSoftwareCatalogAsTable: SoftwareCatalog.FetchAsTable get() = GoogleProvidedSoftwareCatalogAsTable

data class SoftwareCatalog(
    val orchestratorVersion: String
) {
    interface Fetch : () -> ProvidedSoftwareCatalog
    interface FetchAsTable : () -> String
}
