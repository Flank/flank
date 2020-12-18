package flank.scripts.dependencies.update

import flank.scripts.utils.Version
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DependenciesResultCheck(
    val current: Dependencies,
    val outdated: Dependencies,
    val gradle: GradleDependency
)

@Serializable
data class Dependency(
    val group: String,
    val version: Version,
    val name: String? = null,
    @SerialName("available") val availableVersion: AvailableVersion? = null
)

@Serializable
data class Dependencies(
    val dependencies: List<Dependency>
)

@Serializable
data class AvailableVersion(
    val release: Version?,
    val milestone: Version?,
    val integration: Version?
)
