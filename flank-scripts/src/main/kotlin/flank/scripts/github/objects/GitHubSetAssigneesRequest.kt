package flank.scripts.github.objects

import kotlinx.serialization.Serializable

@Serializable
data class GitHubSetAssigneesRequest(
    val assignees: List<String>
)
