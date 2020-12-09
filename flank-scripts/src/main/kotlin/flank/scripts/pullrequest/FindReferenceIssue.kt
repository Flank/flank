package flank.scripts.pullrequest

import flank.scripts.github.objects.GithubPullRequest

fun GithubPullRequest.findReferenceNumber() =
    (tryGetReferenceNumberFromBody() ?: tryGetReferenceNumberFromBranch())
        ?.trim()
        ?.replace("#", "")
        ?.toInt()

private fun GithubPullRequest.tryGetReferenceNumberFromBody() = bodyReferenceRegex.find(body)?.value

private fun GithubPullRequest.tryGetReferenceNumberFromBranch() = branchReferenceRegex.find(head?.ref.orEmpty())?.value

private val bodyReferenceRegex = "#\\d+\\s".toRegex()
private val branchReferenceRegex = "#\\d+".toRegex()
