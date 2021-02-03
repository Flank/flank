package flank.scripts.cli.testartifacts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import flank.scripts.ops.testartifacts.Context
import flank.scripts.ops.testartifacts.linkArtifacts

object LinkCommand : CliktCommand(
    help = "Create symbolic link to under test_runner/src/test/kotlin/ftl/fixtures/tmp to test_artifacts/{branchName}."
) {
    private val artifacts by requireObject<Context>()

    override fun run() {
        artifacts.linkArtifacts()
    }
}
