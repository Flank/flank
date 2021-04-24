package ftl.domain

import flank.common.logLn
import ftl.api.fetchSoftwareCatalog
import ftl.environment.common.toCliTable

interface ListProvidedSoftware

operator fun ListProvidedSoftware.invoke() {
    logLn(fetchSoftwareCatalog().toCliTable())
}
