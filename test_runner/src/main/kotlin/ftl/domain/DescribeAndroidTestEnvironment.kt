package ftl.domain

import flank.common.logLn
import ftl.android.AndroidCatalog
import ftl.args.AndroidArgs
import ftl.environment.ipBlocksListAsTable
import ftl.environment.networkConfigurationAsTable
import ftl.environment.providedSoftwareAsTable
import java.nio.file.Paths

interface DescribeAndroidTestEnvironment {
    val configPath: String
}

fun DescribeAndroidTestEnvironment.invoke() {
    val projectId = AndroidArgs.loadOrDefault(Paths.get(configPath)).project
    logLn(AndroidCatalog.devicesCatalogAsTable(projectId))
    logLn(AndroidCatalog.supportedVersionsAsTable(projectId))
    logLn(AndroidCatalog.localesAsTable(projectId))
    logLn(providedSoftwareAsTable())
    logLn(networkConfigurationAsTable())
    logLn(AndroidCatalog.supportedOrientationsAsTable(projectId))
    logLn(ipBlocksListAsTable())
}
