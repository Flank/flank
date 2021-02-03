package flank.scripts.ops.integration

data class IntegrationContext(
    val result: ITResults,
    val token: String,
    val url: String,
    val runID: String,
    val lastRun: String,
    val openedIssue: Int?,
) {
    val issueNumber: Int
        get() = requireNotNull(openedIssue)
}
