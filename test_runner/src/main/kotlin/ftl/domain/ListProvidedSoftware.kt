package ftl.domain

import flank.common.logLn
import ftl.api.fetchSoftwareCatalogAsTable

interface ListProvidedSoftware

operator fun ListProvidedSoftware.invoke() {
    logLn(fetchSoftwareCatalogAsTable())
}
