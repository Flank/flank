package ftl.domain

import flank.common.logLn
import ftl.args.IosArgs
import ftl.environment.ipBlocksList
import ftl.environment.networkConfigurationAsTable
import ftl.environment.providedSoftwareAsTable
import ftl.environment.toCliTable
import ftl.ios.IosCatalog
import java.nio.file.Paths

interface DescribeIosTestEnvironment {
    val configPath: String
}

operator fun DescribeIosTestEnvironment.invoke() {
    val projectId = IosArgs.loadOrDefault(Paths.get(configPath)).project
    logLn(IosCatalog.devicesCatalogAsTable(projectId))
    logLn(IosCatalog.softwareVersionsAsTable(projectId))
    logLn(IosCatalog.localesAsTable(projectId))
    logLn(providedSoftwareAsTable())
    logLn(networkConfigurationAsTable())
    logLn(IosCatalog.supportedOrientationsAsTable(projectId))
    // TODO move toCliTable() to presentation layer during refactor of presentation after #1728
    logLn(ipBlocksList().toCliTable())
}
