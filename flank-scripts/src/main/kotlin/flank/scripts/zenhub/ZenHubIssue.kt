package flank.scripts.zenhub

import com.github.kittinunf.fuel.core.ResponseDeserializable
import flank.scripts.utils.toObject
import kotlinx.serialization.Serializable

@Serializable
data class ZenHubIssue(
    val estimate: ZenHubEstimate
)

@Serializable
data class ZenHubEstimate(
    val value: Int
)

object ZenHubIssueDeserializable : ResponseDeserializable<ZenHubIssue> {
    override fun deserialize(content: String): ZenHubIssue = content.toObject()
}
