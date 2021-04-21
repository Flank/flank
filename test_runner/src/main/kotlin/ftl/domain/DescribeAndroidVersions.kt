package ftl.domain

import flank.common.logLn
import ftl.args.AndroidArgs
import ftl.client.google.AndroidCatalog
import ftl.run.exception.FlankConfigurationError
import java.nio.file.Paths

interface DescribeAndroidVersions {
    val versionId: String
    val configPath: String
}

fun DescribeAndroidVersions.invoke() {
    if (versionId.isBlank()) throw FlankConfigurationError("Argument VERSION_ID must be specified.")
    logLn(AndroidCatalog.describeSoftwareVersion(AndroidArgs.loadOrDefault(Paths.get(configPath)).project, versionId))
}
