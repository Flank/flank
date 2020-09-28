package flank.scripts.release.updatebugsnag

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.coroutines.awaitResult
import flank.scripts.exceptions.mapClientError
import flank.scripts.exceptions.toBugsnagException
import flank.scripts.utils.toJson

suspend fun updateBugsnag(bugsnagApiKey: String, appVersion: String, githubWorkflowUrl: String) =
        httpRequest(createRequestBody(bugsnagApiKey, appVersion, githubWorkflowUrl))

private suspend fun httpRequest(jsonString: String) =
        Fuel.post(BUGNSAG_URL)
                .jsonBody(jsonString)
                .awaitResult(BugSnagResponseDeserializer)
                .mapClientError { it.toBugsnagException() }

private fun createRequestBody(bugsnagApiKey: String, appVersion: String, githubWorkflowUrl: String) =
        BugSnagRequest(
            apiKey = bugsnagApiKey,
            appVersion = appVersion,
            releaseStage = "production",
            builderName = "github-actions",
            sourceControl = githubActionsSourceControl(appVersion),
            metadata = mapOf("github_actions_build_url" to githubWorkflowUrl)
        ).toJson()

private fun githubActionsSourceControl(appVersion: String) = SourceControl(
    "github",
    REPOSITORY,
    appVersion
)

private const val BUGNSAG_URL = "https://build.bugsnag.com/"
private const val REPOSITORY = "https://github.com/Flank/flank"
