package flank.scripts.integration

data class IntegrationContext(
    val result: ITResults,
    val token: String,
    val url: String,
    val runID: String,
    val lastRun: String,
    private val openedIssue: Int?,
) {
    val issueNumber: Int
        get() = requireNotNull(openedIssue)
}
