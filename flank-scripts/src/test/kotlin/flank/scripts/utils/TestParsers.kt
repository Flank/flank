package flank.scripts.utils

import flank.scripts.dependencies.update.AvailableVersion
import flank.scripts.dependencies.update.Dependency
import flank.scripts.dependencies.update.DependencyUpdate
import flank.scripts.dependencies.update.GradleReleaseChannel

fun toGradleReleaseChannel(
    version: String,
    reason: String,
    isUpdateAvailable: Boolean,
    isFailure: Boolean
) = GradleReleaseChannel(parseToVersion(version), reason, isUpdateAvailable, isFailure)

fun toDependency(
    group: String,
    version: String,
    name: String? = null,
    availableVersion: AvailableVersion? = null
) = Dependency(group, parseToVersion(version), name, availableVersion)

fun toDependencyUpdate(
    name: String,
    valName: String,
    oldVersion: String,
    newVersion: String
) = DependencyUpdate(name, valName, parseToVersion(oldVersion), parseToVersion(newVersion))

fun toAvailableVersion(
    release: String?,
    milestone: String?,
    integration: String?
) = AvailableVersion(release?.let(::parseToVersion), milestone?.let(::parseToVersion), integration?.let(::parseToVersion))
