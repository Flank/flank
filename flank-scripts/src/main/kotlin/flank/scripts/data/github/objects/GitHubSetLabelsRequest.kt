package flank.scripts.data.github.objects

import kotlinx.serialization.Serializable

@Serializable
data class GitHubSetLabelsRequest(
    val labels: List<String>
)
