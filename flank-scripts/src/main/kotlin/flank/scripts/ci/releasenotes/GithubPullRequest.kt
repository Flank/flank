package flank.scripts.ci.releasenotes

import com.github.kittinunf.fuel.core.ResponseDeserializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class GithubPullRequest(
    @SerialName("html_url") val htmlUrl: String,
    val title: String,
    val number: Int,
    val assignees: List<GithubUser>
)

@Serializable
data class GithubUser(
    val login: String,
    @SerialName("html_url") val htmlUrl: String
)

object GithubPullRequestDeserializer : ResponseDeserializable<List<GithubPullRequest>> {
    override fun deserialize(content: String): List<GithubPullRequest> {
        return Json.decodeFromString(content)
    }
}
