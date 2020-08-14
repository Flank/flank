package flank.scripts.ci.nexttag

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ci.getLatestReleaseTag
import kotlinx.coroutines.runBlocking

class NextReleaseTagCommand : CliktCommand(help = "Set next release tag variable", name = "nextReleaseTag") {

    private val token by option(help = "Git Token").required()

    override fun run() {
        runBlocking {
            getNextReleaseTagCommand(token)
        }
    }
}

private suspend fun getNextReleaseTagCommand(token: String) {
    println(generateNextReleaseTag(getLatestReleaseTag(token)))
}
