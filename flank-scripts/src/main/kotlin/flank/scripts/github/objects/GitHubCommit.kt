package flank.scripts.github.objects

import com.github.kittinunf.fuel.core.ResponseDeserializable
import flank.scripts.utils.toObject
import kotlinx.serialization.Serializable

@Serializable
data class GitHubCommit(
    val sha: String
)

object GitHubCommitListDeserializer : ResponseDeserializable<List<GitHubCommit>> {
    override fun deserialize(content: String): List<GitHubCommit> = content.toObject()
}
