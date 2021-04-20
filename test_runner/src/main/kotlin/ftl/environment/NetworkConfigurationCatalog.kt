package ftl.environment

import ftl.environment.common.toCliTable
import ftl.gc.GcTesting
import ftl.http.executeWithRetry

fun networkConfigurationAsTable() = getNetworkConfiguration().toCliTable()

fun getNetworkConfiguration() = GcTesting.get.testEnvironmentCatalog()
    .get("NETWORK_CONFIGURATION")
    .executeWithRetry()
    ?.networkConfigurationCatalog
    ?.configurations
    .orEmpty()
