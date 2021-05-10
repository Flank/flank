package ftl.client.google

import ftl.environment.common.toCliTable
import ftl.http.executeWithRetry

fun providedSoftwareAsTable() = getProvidedSoftware().toCliTable()

internal fun getProvidedSoftware() = GcTesting.get.testEnvironmentCatalog()
    .get("provided_software")
    .executeWithRetry()
    .softwareCatalog
