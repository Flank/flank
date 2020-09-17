package flank.scripts.dependencies.update

import kotlinx.serialization.Serializable

@Serializable
data class GradleDependency(
    val current: GradleVersion,
    val nightly: GradleVersion,
    val releaseCandidate: GradleVersion,
    val running: GradleVersion
)

@Serializable
data class GradleVersion(
    val version: String,
    val reason: String,
    val isUpdateAvailable: Boolean,
    val isFailure: Boolean
)
