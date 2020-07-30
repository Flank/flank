package flank.scripts.exceptions

import flank.scripts.release.hub.GitHubErrorResponse
import flank.scripts.release.updatebugsnag.BugSnagResponse

sealed class FlankScriptsExceptions() : Exception()

class GitHubException(private val body: GitHubErrorResponse) : FlankScriptsExceptions() {
    override fun toString(): String {
        return "Error while doing GitHub request, because of ${body.message}, more info at ${body.documentationUrl}"
    }
}

class BugsnagException(private val body: BugSnagResponse) : FlankScriptsExceptions() {
    override fun toString(): String {
        return "Error while doing Bugnsag request, because of ${body.errors.joinToString()}"
    }
}
