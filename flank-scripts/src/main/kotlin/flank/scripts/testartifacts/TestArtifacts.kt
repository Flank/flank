@file:Suppress("ImplicitThis")

package flank.scripts.testartifacts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findOrSetObject
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import flank.scripts.testartifacts.core.Context
import flank.scripts.testartifacts.core.downloadFixtures
import flank.scripts.testartifacts.core.flankRoot
import flank.scripts.testartifacts.core.linkArtifacts
import flank.scripts.testartifacts.core.prepareTestArtifacts
import flank.scripts.testartifacts.core.unzipTestArtifacts
import flank.scripts.testartifacts.core.uploadFixtures
import flank.scripts.testartifacts.core.zipTestArtifacts
import flank.scripts.utils.currentGitBranch
import java.io.File

class TestArtifactsCommand : CliktCommand(
    name = "testArtifacts"
) {

    private val branch: String by option(
        "-b",
        help = "Define on which branch the command should operate. The current git branch is a default."
    ).default(currentGitBranch())

    private val projectRoot: File by option(
        "-p",
        help = "Define project root directory. The "
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
            LinkArtifacts()
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
    override fun run() {
        artifacts.downloadFixtures()
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
    help = "Creates fresh copy of master's test artifacts for current working branch."
) {
    val artifacts by requireObject<Context>()
    override fun run() {
        artifacts.prepareTestArtifacts()
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

private class LinkArtifacts : CliktCommand(
    name = "link",
    help = "Create symbolic link to under test_runner/src/test/kotlin/ftl/fixtures/tmp to test_artifacts/{branchName}"
) {
    val artifacts by requireObject<Context>()
    override fun run() {
        artifacts.linkArtifacts()
    }
}
