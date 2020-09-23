package flank.scripts.release.updatebugsnag

import com.github.kittinunf.fuel.core.ResponseDeserializable
import flank.scripts.utils.toObject
import kotlinx.serialization.Serializable

@Serializable
data class BugSnagResponse(
    val status: String,
    val warnings: List<String> = listOf(),
    val errors: List<String> = listOf()
)

object BugSnagResponseDeserializer : ResponseDeserializable<BugSnagResponse> {
    override fun deserialize(content: String): BugSnagResponse = content.toObject()
}
