package ftl.domain

import flank.common.logLn
import ftl.adapter.google.asPrintableTable
import ftl.api.Locale.Identity
import ftl.api.Platform
import ftl.api.fetchIpBlocks
import ftl.api.fetchLocales
import ftl.api.fetchOrientation
import ftl.args.AndroidArgs
import ftl.client.google.AndroidCatalog
import ftl.environment.common.toCliTable
import ftl.environment.networkConfigurationAsTable
import ftl.environment.providedSoftwareAsTable
import ftl.environment.toCliTable
import java.nio.file.Paths

interface DescribeAndroidTestEnvironment {
    val configPath: String
}

fun DescribeAndroidTestEnvironment.invoke() {
    val projectId = AndroidArgs.loadOrDefault(Paths.get(configPath)).project
    logLn(AndroidCatalog.devicesCatalogAsTable(projectId))
    logLn(AndroidCatalog.supportedVersionsAsTable(projectId))
    logLn(fetchLocales(Identity(projectId, Platform.ANDROID)).asPrintableTable())
    logLn(providedSoftwareAsTable())
    logLn(networkConfigurationAsTable())
    // TODO move toCliTable() to presentation layer during refactor of presentation after #1728
    logLn(fetchOrientation(projectId, Platform.ANDROID).toCliTable())
    logLn(fetchIpBlocks().toCliTable())
}
