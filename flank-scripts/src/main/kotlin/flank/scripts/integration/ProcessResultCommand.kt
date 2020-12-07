package flank.scripts.integration

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import com.github.kittinunf.result.getOrNull
import com.github.kittinunf.result.onError
import flank.scripts.github.objects.GitHubCreateIssueCommentRequest
import flank.scripts.github.objects.GitHubCreateIssueRequest
import flank.scripts.github.objects.GitHubUpdateIssueRequest
import flank.scripts.github.objects.IssueState
import flank.scripts.github.patchIssue
import flank.scripts.github.postNewIssue
import flank.scripts.github.postNewIssueComment
import flank.scripts.utils.toJson
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

@FlowPreview
object ProcessResultCommand : CliktCommand(name = "processResults") {

    private val itResult by option(help = "IT run job status", names = arrayOf("--result"))
        .enum<ITResults>(ignoreCase = true)
        .required()

    private val buildScanURL by option(help = "Gradle build scan URL", names = arrayOf("--url"))
        .required()

    private val githubToken by option(help = "Git Token").required()

    private val runID by option(help = "Workflow job ID").required()

    override fun run() {
        println(
            """
            ** Parameters:
                 token:  $githubToken
                 result: $itResult
                 url:    $buildScanURL
                 runID:  $runID
        """.trimIndent()
        )
        runBlocking {
            val openedIssue = checkForOpenedITIssues(githubToken)
            val lastRun = getLastITWorkflowRunDate(githubToken)

            when {
                itResult == ITResults.FAILURE && openedIssue == null -> createNewIssue(lastRun)
                itResult == ITResults.FAILURE && openedIssue != null -> postFailureComment(openedIssue.number, lastRun)
                itResult == ITResults.SUCCESS && openedIssue != null -> closeIssue(openedIssue.number, lastRun)
                else -> return@runBlocking
            }
        }
    }

    private suspend fun createNewIssue(lastRun: String) = coroutineScope {
        println("** Creating new issue")
        val issuePayload = GitHubCreateIssueRequest(
            title = "Full Suite integration tests failed on master",
            body = "### Integration Test failed on master",
            labels = listOf("IT_Failed", "bug")
        )
        println(issuePayload.toJson())
        val issue = postNewIssue(githubToken, issuePayload)
            .onError {
                println(it.message)
            }
            .getOrNull()
        if (issue == null) {
            println("** There were problems with issue creation")
            exitProcess(-1)
        }
        println(
            """
            ** Issue created:
                 url:    ${issue.htmlUrl}
                 number: ${issue.number}
        """.trimIndent()
        )
        postFailureComment(issue.number, lastRun)
    }

    private suspend fun postFailureComment(issueNumber: Int, lastRun: String) =
        coroutineScope { postComment(issueNumber, lastRun, Failure) }

    private suspend fun postSuccessComment(issueNumber: Int, lastRun: String) =
        coroutineScope { postComment(issueNumber, lastRun, Success) }

    private suspend inline fun postComment(issueNumber: Int, lastRun: String, type: CommentMessage) = coroutineScope {
        val message = when (type) {
            is Success -> prepareSuccessMessage(lastRun, runID, buildScanURL)
            is Failure -> {
                val commitList = getCommitListSinceDate(githubToken, lastRun)
                prepareFailureMessage(lastRun, runID, buildScanURL, commitList)
            }
        }
        val payload = GitHubCreateIssueCommentRequest(message)
        println("** Comment created")
        println(payload.toJson())
        postNewIssueComment(githubToken, issueNumber, payload)
    }

    private suspend fun closeIssue(issueNumber: Int, lastRun: String) = coroutineScope {
        postSuccessComment(issueNumber, lastRun)
        println("** Closing issue")
        patchIssue(githubToken, issueNumber, GitHubUpdateIssueRequest(state = IssueState.CLOSED))
    }

    private enum class ITResults {
        SUCCESS, FAILURE
    }
}
