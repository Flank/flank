package flank.scripts.exceptions

import flank.scripts.github.GitHubErrorResponse
import flank.scripts.release.updatebugsnag.BugSnagResponse

sealed class FlankScriptsExceptions : Exception()

class GitHubException(val body: GitHubErrorResponse) : FlankScriptsExceptions() {
    override fun toString(): String {
        return "Error while doing GitHub request, because of ${body.message}, more info at ${body.documentationUrl}"
    }
}

class BugsnagException(val body: BugSnagResponse) : FlankScriptsExceptions() {
    override fun toString(): String {
        return "Error while doing Bugnsag request, because of ${body.errors.joinToString()}"
    }
}

class ShellCommandException(private val errorMessage: String) : FlankScriptsExceptions() {
    override fun toString(): String {
        return "Error while executing shell command, details: $errorMessage"
    }
}
