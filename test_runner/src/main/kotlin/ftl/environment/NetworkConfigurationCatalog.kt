package ftl.environment

import ftl.gc.GcTesting
import ftl.http.executeWithRetry

fun networkConfigurationAsTable() = networkConfiguration().asPrintableTable()

private fun networkConfiguration() = GcTesting.get.testEnvironmentCatalog()
    .get("NETWORK_CONFIGURATION")
    .executeWithRetry()
    ?.networkConfigurationCatalog
    ?.configurations
    .orEmpty()
