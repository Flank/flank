package flank.scripts.github.objects

import com.github.kittinunf.fuel.core.ResponseDeserializable
import flank.scripts.utils.toObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubCreateIssueCommentRequest(
    val body: String
)

@Serializable
data class GitHubCreateIssueCommentResponse(
    val id: Int,
    @SerialName("html_url")
    val htmlUrl: String,
    val body: String,
)

object GitHubCreateIssueCommentResponseDeserializer : ResponseDeserializable<GitHubCreateIssueCommentResponse> {
    override fun deserialize(content: String): GitHubCreateIssueCommentResponse = content.toObject()
}
