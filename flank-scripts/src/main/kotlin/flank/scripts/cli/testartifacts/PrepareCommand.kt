package flank.scripts.cli.testartifacts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import flank.scripts.ops.testartifacts.Context
import flank.scripts.ops.testartifacts.prepareTestArtifacts

object PrepareCommand : CliktCommand(
    help = "Creates fresh copy of test artifacts for current working branch, basing on existing one."
) {
    val artifacts by requireObject<Context>()
    val source by option(
        "--src", "-s",
        help = "The name of branch that identify artifacts source. The master branch is a default."
    ).default("master")

    override fun run() {
        artifacts.prepareTestArtifacts(source)
    }
}
