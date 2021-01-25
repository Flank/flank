package flank.scripts.ops.integration

import flank.scripts.cli.ITResults

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
