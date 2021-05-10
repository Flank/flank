package ftl.client.google

import ftl.http.executeWithRetry

fun getGoogleNetworkConfiguration() = GcTesting.get.testEnvironmentCatalog()
    .get("NETWORK_CONFIGURATION")
    .executeWithRetry()
    ?.networkConfigurationCatalog
    ?.configurations
    .orEmpty()
