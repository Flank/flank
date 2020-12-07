package flank.scripts.github.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubUpdateIssueRequest(
    val title: String = "",
    val body: String = "",
    val state: IssueState = IssueState.OPEN,
    val milestone: Int? = null,
    val labels: List<String> = emptyList(),
    val assignees: List<String> = emptyList()
)

@Serializable
enum class IssueState {
    @SerialName("open") OPEN,
    @SerialName("closed") CLOSED
}
