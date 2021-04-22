package ftl.domain

import flank.common.logLn
import ftl.api.fetchSoftwareCatalog
import ftl.environment.common.asTable

interface ListProvidedSoftware

operator fun ListProvidedSoftware.invoke() {
    logLn(fetchSoftwareCatalog().asTable())
}
