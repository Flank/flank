package ftl.domain

import flank.common.logLn
import ftl.environment.providedSoftwareAsTable

interface ListProvidedSoftware

operator fun ListProvidedSoftware.invoke() {
    logLn(providedSoftwareAsTable())
}
