package flank.scripts.release.hub

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.kittinunf.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RemoveOldTagCommand : CliktCommand(name = ".", help = "Remove old tag on GitHub") {

    private val gitTag by option(help = "Git Tag").required()
    private val username by option(help = "Git User").required()
    private val token by option(help = "Git Token").required()

    override fun run() {
        runBlocking {
            when(val response = withContext(Dispatchers.Default) { removeOldTag(gitTag, username, token) }) {
                is Result.Success -> println("Tag $gitTag was removed")
                is Result.Failure -> println(response.error)
            }
        }
    }
}
