package flank.scripts.pullrequest

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

object PullRequestCommand : CliktCommand(name = "pullRequest") {

    init {
        subcommands(CopyProperties)
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {}
}
