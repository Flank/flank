package flank.scripts.ops.ci.releasenotes

import com.github.kittinunf.fuel.core.ResponseDeserializable
import flank.scripts.utils.toObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRelease(
    @SerialName("tag_name") val tag: String
)

object GithubReleaseDeserializable : ResponseDeserializable<GitHubRelease> {
    override fun deserialize(content: String): GitHubRelease = content.toObject()
}
