package flank.scripts.zenhub.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateEpicRequest(
    @SerialName("add_issues")
    val toAdd: List<Issue>,
    @SerialName("remove_issues")
    val toRemove: List<Issue>
)

@Serializable
data class ConvertToEpicRequest(
    @SerialName("issues")
    val issueList: List<Issue>
)

@Serializable
data class Issue(
    @SerialName("repo_id")
    val repo: Int,
    @SerialName("issue_number")
    val issueNumber: Int
)
