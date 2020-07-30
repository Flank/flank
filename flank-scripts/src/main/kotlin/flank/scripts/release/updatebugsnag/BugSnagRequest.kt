package flank.scripts.release.updatebugsnag

import kotlinx.serialization.Serializable

@Serializable
data class BugSnagRequest(
    val apiKey: String,
    val appVersion: String,
    val releaseStage: String,
    val builderName: String,
    val sourceControl: SourceControl,
    val metadata: Map<String, String> = mapOf()
)

@Serializable
data class SourceControl(
    val provider: String,
    val repository: String,
    val revision: String
)
