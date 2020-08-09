package ftl.environment

import ftl.environment.common.asPrintableTable
import ftl.gc.GcTesting
import ftl.http.executeWithRetry

fun networkConfigurationAsTable() = getNetworkConfiguration().asPrintableTable()

fun getNetworkConfiguration() = GcTesting.get.testEnvironmentCatalog()
        .get("NETWORK_CONFIGURATION")
        .executeWithRetry()
        ?.networkConfigurationCatalog
        ?.configurations
        .orEmpty()
