package flank.scripts.ops.dependencies.common

import flank.scripts.utils.Version
import kotlinx.serialization.Serializable

@Serializable
data class GradleDependency(
    val current: GradleReleaseChannel,
    val nightly: GradleReleaseChannel,
    val releaseCandidate: GradleReleaseChannel,
    val running: GradleReleaseChannel
)

@Serializable
data class GradleReleaseChannel(
    val version: Version,
    val reason: String,
    val isUpdateAvailable: Boolean,
    val isFailure: Boolean
)
