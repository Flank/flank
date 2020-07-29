package flank.scripts.release.updatebugsnag

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.kittinunf.result.Result
import flank.scripts.utils.ERROR_WHEN_RUNNING
import flank.scripts.utils.SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.system.exitProcess

class UpdateBugSnagCommand : CliktCommand(name = "updateBugsnag", help = "Update Bugnsag") {

    private val bugsnagApiKey by option(help = "Bugsnag api key").required()
    private val appVersion by option(help = "App version to update").required()
    private val githubWorkflowUrl by option(help = "GitHub workflow url")

    override fun run() {
        runBlocking {
            withContext(Dispatchers.IO) {
                when (val response = updateBugsnag(bugsnagApiKey, appVersion, githubWorkflowUrl.orEmpty())) {
                    is Result.Success -> {
                        println("Bugsnag was updated")
                        exitProcess(SUCCESS)
                    }
                    is Result.Failure -> {
                        println(response.error)
                        exitProcess(ERROR_WHEN_RUNNING)
                    }
                }
            }
        }
    }
}
