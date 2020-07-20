package ftl.environment

import ftl.gc.GcTesting
import ftl.http.executeWithRetry

fun providedSoftwareAsTable() = getProvidedSoftware().asTable()

private fun getProvidedSoftware() = GcTesting.get.testEnvironmentCatalog()
    .get("provided_software")
    .executeWithRetry()
    .softwareCatalog
