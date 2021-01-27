package flank.scripts.cli.release

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import flank.scripts.ops.release.hub.deleteOldRelease
import flank.scripts.ops.release.hub.tryDeleteOldTag
import flank.scripts.ops.release.hub.tryReleaseFlank
import flank.scripts.ops.release.jfrog.jFrogDeleteOldSnapshot
import flank.scripts.ops.release.jfrog.jFrogSync
import kotlin.system.exitProcess

class ReleaseCommand : CliktCommand(name = "release", help = "Contains all release commands") {

    init {
        subcommands(
            ReleaseFlankCommand(),
            DeleteOldSnapshotCommand(),
            SyncMavenCommand(),
            DeleteOldReleaseCommand(),
            DeleteOldTagCommand()
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}

class ReleaseFlankCommand : CliktCommand(name = "releaseFlank", help = "Release Flank on GitHub") {

    private val inputFile by option(help = "Path to release file").path(mustExist = true).required()
    private val snapshot by option(help = "Is Snapshot release").flag(default = false)
    private val gitTag by option(help = "Git Tag").required()
    private val commitHash by option(help = "Git Commit hash").default("")
    private val token by option(help = "Git Token").default("")

    override fun run() {
        exitProcess(tryReleaseFlank(inputFile, gitTag, commitHash, snapshot, token))
    }
}

class DeleteOldTagCommand : CliktCommand(name = "deleteOldTag", help = "Delete old tag on GitHub") {

    private val gitTag by option(help = "Git Tag").required()
    private val username by option(help = "Git User").required()
    private val token by option(help = "Git Token").required()

    override fun run() {
        tryDeleteOldTag(
            gitTag,
            username,
            token,
            success = {
                println("Tag $gitTag was deleted")
            },
            error = {
                println(it.error)
            }
        )
    }
}

class DeleteOldReleaseCommand : CliktCommand(name = "deleteOldRelease", help = "Delete old release on github") {

    private val gitTag by option(help = "Git Tag").required()

    override fun run() {
        deleteOldRelease(gitTag)
    }
}

class DeleteOldSnapshotCommand : CliktCommand(name = "jFrogDelete", help = "Delete old version on bintray") {

    private val version by option(help = "Maven version to delete").required()

    override fun run() {
        jFrogDeleteOldSnapshot(version)
    }
}

class SyncMavenCommand : CliktCommand(name = "jFrogSync", help = "Sync maven repository using jfrog") {

    private val mavenTag by option(help = "Maven Tag").required()

    override fun run() {
        exitProcess(jFrogSync(mavenTag))
    }
}
