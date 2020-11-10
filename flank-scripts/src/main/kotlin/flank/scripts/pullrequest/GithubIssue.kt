package flank.scripts.pullrequest

import com.github.kittinunf.fuel.core.ResponseDeserializable
import flank.scripts.utils.toObject
import kotlinx.serialization.Serializable

@Serializable
data class GitHubPullRequest(
    val assignees: List<GitHubUser> = emptyList(),
    val labels: List<GitHubLabel> = emptyList(),
    val body: String = "",
    val head: GitHubHead?
)

object GitHubPullRequestDeserializable : ResponseDeserializable<GitHubPullRequest> {
    override fun deserialize(content: String): GitHubPullRequest = content.toObject()
}

@Serializable
data class GitHubLabel(
    val name: String
)

object GitHubLabelDeserializable : ResponseDeserializable<List<GitHubLabel>> {
    override fun deserialize(content: String): List<GitHubLabel> = content.toObject()
}

@Serializable
data class GitHubUser(
    val login: String
)

@Serializable
data class GitHubHead(
    val ref: String
)
