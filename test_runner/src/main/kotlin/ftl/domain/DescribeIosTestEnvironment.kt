package ftl.domain

import flank.common.logLn
import ftl.args.IosArgs
import ftl.environment.ipBlocksListAsTable
import ftl.environment.networkConfigurationAsTable
import ftl.environment.providedSoftwareAsTable
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
    logLn(ipBlocksListAsTable())
}
