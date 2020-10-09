package flank.scripts.testartifacts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findOrSetObject
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import flank.scripts.testartifacts.core.Context
import flank.scripts.testartifacts.core.downloadFixtures
import flank.scripts.testartifacts.core.flankRoot
import flank.scripts.testartifacts.core.linkArtifacts
import flank.scripts.testartifacts.core.prepareTestArtifacts
import flank.scripts.testartifacts.core.removeRemoteCopy
import flank.scripts.testartifacts.core.resolveArtifacts
import flank.scripts.testartifacts.core.unzipTestArtifacts
import flank.scripts.testartifacts.core.uploadFixtures
import flank.scripts.testartifacts.core.zipTestArtifacts
import flank.scripts.utils.currentGitBranch
import java.io.File

class TestArtifactsCommand : CliktCommand(
    name = "testArtifacts",
    help = "The base command for artifacts management."
) {

    private val branch: String by option(
        "--branch", "-b",
        help = "Branch name that identify test artifacts to operate. The current git branch is a default."
    ).default(currentGitBranch())

    private val projectRoot: File by option(
        "--project-root", "-p",
        help = "Path to local project repository root. By default it is resolved from FLANK_ROOT env variable."
    ).file().default(flankRoot())

    private val artifacts by findOrSetObject {
        Context(
            branch = branch,
            projectRoot = projectRoot
        )
    }

    init {
        subcommands(
            DownloadCommand(),
            UploadCommand(),
            PrepareCommand(),
            ZipCommand(),
            UnzipCommand(),
            LinkCommand(),
            RemoveCommand(),
            ResolveCommand()
        )
    }

    override fun run() {
        artifacts
    }
}

private class DownloadCommand : CliktCommand(
    help = "Download test artifacts zip asset to test_artifacts directory."
) {
    val artifacts by requireObject<Context>()
    val overwrite by option(
        "--overwrite", "-o",
        help = "Flag which indicates if should overwrite old resources when downloading"
    ).flag()

    override fun run() {
        artifacts.downloadFixtures(overwrite)
    }
}

private class UploadCommand : CliktCommand(
    help = "Upload test artifacts zip as github release asset."
) {
    val artifacts by requireObject<Context>()
    override fun run() {
        artifacts.uploadFixtures()
    }
}

private class PrepareCommand : CliktCommand(
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

private class ZipCommand : CliktCommand(
    help = "Create zip archive from test artifacts directory."
) {
    val artifacts by requireObject<Context>()
    override fun run() {
        artifacts.zipTestArtifacts()
    }
}

private class UnzipCommand : CliktCommand(
    help = "Unpack test artifacts zip archive."
) {
    val artifacts by requireObject<Context>()
    override fun run() {
        artifacts.unzipTestArtifacts()
    }
}

private class LinkCommand : CliktCommand(
    help = "Create symbolic link to under test_runner/src/test/kotlin/ftl/fixtures/tmp to test_artifacts/{branchName}."
) {
    val artifacts by requireObject<Context>()
    override fun run() {
        artifacts.linkArtifacts()
    }
}

private class RemoveCommand : CliktCommand(
    help = "Remove remote copy of test artifacts."
) {
    val artifacts by requireObject<Context>()
    override fun run() {
        artifacts.removeRemoteCopy()
    }
}

private class ResolveCommand : CliktCommand(
    help = "Automatically prepare local artifacts if needed."
) {
    val artifacts by requireObject<Context>()
    override fun run() {
        artifacts.resolveArtifacts()
    }
}
