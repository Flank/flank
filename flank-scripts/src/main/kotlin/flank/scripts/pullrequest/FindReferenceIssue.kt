package flank.scripts.pullrequest

fun GitHubPullRequest.findReferenceNumber() =
    (tryGetReferenceNumberFromBody() ?: tryGetReferenceNumberFromBranch())
        ?.trim()
        ?.replace("#", "")
        ?.toInt()

private fun GitHubPullRequest.tryGetReferenceNumberFromBody() = bodyReferenceRegex.find(body)?.value

private fun GitHubPullRequest.tryGetReferenceNumberFromBranch() = branchReferenceRegex.find(head?.ref.orEmpty())?.value

private val bodyReferenceRegex = "#\\d+\\s".toRegex()
private val branchReferenceRegex = "#\\d+".toRegex()
