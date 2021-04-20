package ftl.client.google

import ftl.environment.common.asTable
import ftl.gc.GcTesting
import ftl.http.executeWithRetry

fun providedSoftwareAsTable() = getProvidedSoftware().asTable()

internal fun getProvidedSoftware() = GcTesting.get.testEnvironmentCatalog()
    .get("provided_software")
    .executeWithRetry()
    .softwareCatalog
