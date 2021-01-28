package flank.scripts.data.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubErrorResponse(
    val message: String,
    @SerialName("documentation_url") val documentationUrl: String? = ""
)
