package flank.scripts.github.objects

import kotlinx.serialization.Serializable

@Serializable
data class GitHubSetLabelsRequest(
    val labels: List<String>
)
