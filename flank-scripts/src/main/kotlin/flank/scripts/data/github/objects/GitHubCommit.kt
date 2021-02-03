package flank.scripts.data.github.objects

import com.github.kittinunf.fuel.core.ResponseDeserializable
import flank.scripts.utils.toObject
import kotlinx.serialization.Serializable

@Serializable
data class GitHubCommit(
    val sha: String,
    val commit: Commit
)

@Serializable
data class Commit(
    val author: Author
)

@Serializable
data class Author(
    val name: String,
    val email: String,
    val date: String
)

object GitHubCommitListDeserializer : ResponseDeserializable<List<GitHubCommit>> {
    override fun deserialize(content: String): List<GitHubCommit> = content.toObject()
}
