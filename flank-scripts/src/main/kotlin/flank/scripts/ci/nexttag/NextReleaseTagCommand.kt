package flank.scripts.ci.nexttag

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.kittinunf.result.Result
import flank.scripts.github.getLatestReleaseTag
import kotlin.system.exitProcess
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
    when (val result = getLatestReleaseTag(token)) {
        is Result.Success -> println(generateNextReleaseTag(result.value.tag))
        is Result.Failure -> {
            println(result.error)
            exitProcess(1)
        }
    }
}
