package ftl.domain

import ftl.api.fetchIosOsVersion
import ftl.args.IosArgs
import ftl.presentation.Output
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.FlankGeneralError
import java.nio.file.Paths

interface DescribeIosVersions : Output {
    val configPath: String
    val versionId: String
}

operator fun DescribeIosVersions.invoke() {
    if (versionId.isBlank()) throw FlankConfigurationError("Argument VERSION_ID must be specified.")
    fetchIosOsVersion(IosArgs.loadOrDefault(Paths.get(configPath)).project)
        .find { it.id == versionId }
        ?.out()
        ?: throw FlankGeneralError("ERROR: '$versionId' is not a valid OS version")
}
