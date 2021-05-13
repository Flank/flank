package ftl.client.google

import ftl.http.executeWithRetry

internal fun getProvidedSoftware() = GcTesting.get.testEnvironmentCatalog()
    .get("provided_software")
    .executeWithRetry()
    .softwareCatalog
