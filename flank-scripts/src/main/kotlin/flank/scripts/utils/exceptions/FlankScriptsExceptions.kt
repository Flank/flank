package flank.scripts.utils.exceptions

import flank.scripts.data.github.GitHubErrorResponse

sealed class FlankScriptsExceptions : Exception()

class GitHubException(val body: GitHubErrorResponse) : FlankScriptsExceptions() {

    override val message: String
        get() = toString()

    override fun toString(): String {
        return "Error while doing GitHub request, because of ${body.message}, more info at ${body.documentationUrl}"
    }
}

class ShellCommandException(private val errorMessage: String) : FlankScriptsExceptions() {
    override fun toString(): String {
        return "Error while executing shell command, details: $errorMessage"
    }
}
