package ftl.domain

import flank.common.logLn
import ftl.args.IosArgs
import ftl.client.google.IosCatalog
import ftl.run.exception.FlankConfigurationError
import java.nio.file.Paths

interface DescribeIosVersions {
    val configPath: String
    val versionId: String
}

operator fun DescribeIosVersions.invoke() {
    if (versionId.isBlank()) throw FlankConfigurationError("Argument VERSION_ID must be specified.")
    logLn(IosCatalog.describeSoftwareVersion(IosArgs.loadOrDefault(Paths.get(configPath)).project, versionId))
}
