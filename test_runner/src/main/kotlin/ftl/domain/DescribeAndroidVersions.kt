package ftl.domain

import ftl.api.fetchAndroidOsVersion
import ftl.args.AndroidArgs
import ftl.presentation.Output
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.FlankGeneralError
import java.nio.file.Paths

interface DescribeAndroidVersions : Output {
    val versionId: String
    val configPath: String
}

fun DescribeAndroidVersions.invoke() {
    if (versionId.isBlank()) throw FlankConfigurationError("Argument VERSION_ID must be specified.")

    fetchAndroidOsVersion(AndroidArgs.loadOrDefault(Paths.get(configPath)).project)
        .find { it.id == versionId }
        ?.out()
        ?: throw FlankGeneralError("ERROR: '$versionId' is not a valid OS version")
}
