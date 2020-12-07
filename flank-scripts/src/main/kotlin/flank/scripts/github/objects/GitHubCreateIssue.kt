package flank.scripts.github.objects

import com.github.kittinunf.fuel.core.ResponseDeserializable
import flank.scripts.utils.toObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubCreateIssueRequest(
    val title: String,
    val body: String,
    val milestone: Int? = null,
    val labels: List<String> = emptyList(),
    val assignees: List<String> = emptyList()
)

@Serializable
data class GitHubCreateIssueResponse(
    val id: Int,
    @SerialName("html_url")
    val htmlUrl: String,
    val body: String,
    val number: Int
)

object GitHubCreateIssueResponseDeserializer : ResponseDeserializable<GitHubCreateIssueResponse> {
    override fun deserialize(content: String): GitHubCreateIssueResponse = content.toObject()
}
