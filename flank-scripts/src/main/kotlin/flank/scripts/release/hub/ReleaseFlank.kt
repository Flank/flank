package flank.scripts.release.hub

import flank.scripts.ci.releasenotes.generateReleaseNotes
import flank.scripts.utils.markdownH2
import flank.scripts.utils.runCommand
import java.io.File
import java.nio.file.Path

fun releaseFlank(path: Path, gitTag: String, commitHash: String, isSnapshotRelease: Boolean, token: String): Int {
    val releasePath = moveFlankToReleaseDirectory(path)
    val releaseCommand = if (isSnapshotRelease) {
        hubStableSnapshotCommand(releasePath, gitTag, commitHash)
    } else {
        hubStableReleaseCommand(releasePath, gitTag, token)
    }
    return releaseCommand.runCommand()
}

private fun moveFlankToReleaseDirectory(inputPath: Path) =
    if (inputPath.toFile().renameTo(File(RELEASE_DIRECTORY))) RELEASE_DIRECTORY else inputPath.toAbsolutePath().toString()

private fun hubStableSnapshotCommand(path: String, gitTag: String, commitHash: String) =
    listOf(
        "hub", "release", "create",
        "-p",
        "-a", path,
        "-m", "Flank $gitTag",
        "-m", "Snapshot release for commit $commitHash",
        gitTag
    )

private fun hubStableReleaseCommand(path: String, gitTag: String, token: String) =
    listOf(
        "hub", "release", "create",
        "-a", path,
        "-m", "Flank $gitTag",
        "-m", generateReleaseNotes(token).asReleaseBody(gitTag),
        gitTag
    )

private fun List<String>.asReleaseBody(tag: String) =
    StringBuilder(tag.markdownH2())
        .appendln()
        .apply { this@asReleaseBody.forEach { appendln(it) } }
        .toString()

private const val RELEASE_DIRECTORY = "./RELEASE/flank.jar"
