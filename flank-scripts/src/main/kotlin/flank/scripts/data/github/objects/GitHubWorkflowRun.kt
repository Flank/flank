package flank.scripts.data.github.objects

import com.github.kittinunf.fuel.core.ResponseDeserializable
import flank.scripts.utils.toObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubWorkflowRunsSummary(
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("workflow_runs")
    val workflowRuns: List<GitHubWorkflowRun>
)

@Serializable
data class GitHubWorkflowRun(
    val status: String,
    val conclusion: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("html_url")
    val htmlUrl: String,
    val name: String
)

object GithubWorkflowRunsSummaryDeserializer : ResponseDeserializable<GitHubWorkflowRunsSummary> {
    override fun deserialize(content: String): GitHubWorkflowRunsSummary = content.toObject()
}
