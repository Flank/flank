package flank.scripts.ops.firebase.common

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.onError
import flank.common.config.zenhubRepositoryID
import flank.common.newLine
import flank.scripts.data.github.objects.GitHubCreateIssueRequest
import flank.scripts.data.github.objects.GitHubCreateIssueResponse
import flank.scripts.data.github.objects.GitHubUpdateIssueRequest
import flank.scripts.data.github.patchIssue
import flank.scripts.data.github.postNewIssue
import flank.scripts.data.zenhub.convertIssueToEpic
import flank.scripts.data.zenhub.objects.ConvertToEpicRequest
import flank.scripts.data.zenhub.objects.Issue
import flank.scripts.data.zenhub.objects.UpdateEpicRequest
import flank.scripts.data.zenhub.updateEpic
import flank.scripts.ops.firebase.SDKUpdateContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.system.exitProcess

internal suspend fun SDKUpdateContext.createEpicIssue() = coroutineScope {
    val (updates, subIssues) = createSubIssues()

    println("** Create new epic")
    val epic = createEpic(updates)

    convertIssueToEpic(
        zenhubToken = zenhubToken,
        issueNumber = epic,
        payload = ConvertToEpicRequest(subIssues)
    )
}

internal suspend fun SDKUpdateContext.updateOpenedEpic() = coroutineScope {
    val (updates, subIssues) = createSubIssues()
    requireNotNull(openedIssue)

    println("** Update existing epic")
    patchIssue(
        githubToken = githubToken,
        issueNumber = openedIssue.number,
        payload = GitHubUpdateIssueRequest(
            title = "Implement new gcloud features [$newVersion]",
            body = openedIssue.body + newLine + updates
        )
    )

    updateEpic(
        zenhubToken = zenhubToken,
        issueNumber = openedIssue.number,
        payload = UpdateEpicRequest(toAdd = subIssues, toRemove = emptyList())
    )
}

private suspend fun SDKUpdateContext.createSubIssues() = coroutineScope {
    println("** Create linked issues")
    findNewFeatures().run {
        if (isEmpty()) {
            println("** No new features")
            exitProcess(0)
        }
        joinToString(separator = "$newLine* ", prefix = "* ") to map {
            async {
                postNewIssue(
                    githubToken = githubToken,
                    payload = GitHubCreateIssueRequest(
                        title = it.split(" ").take(10).joinToString(" "),
                        body = "#### Verify and implement:$newLine* $it",
                        labels = listOf("Feature")
                    ),
                ).handleRequest()
            }
        }
            .awaitAll()
            .map { Issue(zenhubRepositoryID, it.number) }
    }
}

private suspend fun SDKUpdateContext.findNewFeatures(): List<String> {
    val text = updatesLazy()

    val allNewFeatures = "(?<=## $newVersion).*([\\s\\S]*)(?=## $oldVersion)".toRegex()
        .find(text)
        ?.groups
        ?.get(1)
        ?.value

    return "(?<=### Firebase Test Lab)(?s).+?((?=##)|(?=Subscribe))".toRegex()
        .findAll(allNewFeatures.orEmpty())
        .flatMap { it.value.trim().split("*") }
        .map { it.trim().replace("\\s+".toRegex(), " ") }
        .filter { it.isNotBlank() }
        .distinct()
        .toList()
}

private suspend fun SDKUpdateContext.createEpic(updates: String) = postNewIssue(
    githubToken = githubToken,
    payload = GitHubCreateIssueRequest(
        title = "Implement new gcloud features [$newVersion]",
        body = body(updates),
        labels = listOf("gcloud SDK", "Feature")
    )
).handleRequest().number

private val body: (String) -> String = {
    "### New features in gcloud sdk found :mag:$newLine#### For more information visit [RELEASE NOTES]" +
        "(https://cloud.google.com/sdk/docs/release-notes#firebase_test_lab) and [SDK REFERENCE]" +
        "(https://cloud.google.com/sdk/gcloud/reference/alpha/firebase)$newLine" +
        "#### Verify and implement:$newLine$it"
}

private fun Result<GitHubCreateIssueResponse, Exception>.handleRequest() = this
    .onError { println(it.message) }
    .get()
    .also(::logIssueCreated)

private fun logIssueCreated(issue: GitHubCreateIssueResponse) = println(
    """
** Issue created:
     url:    ${issue.htmlUrl}
     number: ${issue.number} 
    """.trimIndent()
)
