package flank.scripts.data.github.objects

import com.github.kittinunf.fuel.core.ResponseDeserializable
import flank.scripts.utils.toObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubPullRequest(
    @SerialName("html_url") val htmlUrl: String,
    val title: String,
    val number: Int,
    val assignees: List<GithubUser>,
    val labels: List<GitHubLabel> = emptyList(),
    val body: String = "",
    val head: GitHubHead? = null
)

@Serializable
data class GithubUser(
    val login: String,
    @SerialName("html_url") val htmlUrl: String
)

object GithubPullRequestListDeserializer : ResponseDeserializable<List<GithubPullRequest>> {
    override fun deserialize(content: String): List<GithubPullRequest> = content.toObject()
}

object GithubPullRequestDeserializer : ResponseDeserializable<GithubPullRequest> {
    override fun deserialize(content: String): GithubPullRequest = content.toObject()
}

@Serializable
data class GitHubLabel(
    val name: String
)

object GitHubLabelDeserializable : ResponseDeserializable<List<GitHubLabel>> {
    override fun deserialize(content: String): List<GitHubLabel> = content.toObject()
}

@Serializable
data class GitHubHead(
    val ref: String
)
