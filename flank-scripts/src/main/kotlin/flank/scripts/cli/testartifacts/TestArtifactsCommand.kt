package flank.scripts.cli.testartifacts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findOrSetObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import flank.scripts.ops.testartifacts.Context
import flank.scripts.ops.testartifacts.flankRoot
import flank.scripts.utils.currentGitBranch
import java.io.File

object TestArtifactsCommand : CliktCommand(
    name = "testArtifacts",
    help = "The base command for artifacts management."
) {
    private val branch: String by option(
        "--branch", "-b",
        help = "Branch name that identify test artifacts to operate. The current git branch is a default."
    ).defaultLazy { currentGitBranch() }

    private val projectRoot: File by option(
        "--project-root", "-p",
        help = "Path to local project repository root. By default it is resolved from FLANK_ROOT env variable."
    ).file().defaultLazy { flankRoot() }

    private val artifacts by findOrSetObject {
        Context(
            branch = branch,
            projectRoot = projectRoot
        )
    }

    init {
        subcommands(
            DownloadCommand,
            UploadCommand,
            PrepareCommand,
            ZipCommand,
            UnzipCommand,
            LinkCommand,
            RemoveCommand,
            ResolveCommand
        )
    }

    override fun run() {
        artifacts
    }
}
