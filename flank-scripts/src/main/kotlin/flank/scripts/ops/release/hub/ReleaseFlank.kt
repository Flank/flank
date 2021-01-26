package flank.scripts.ops.release.hub

import flank.scripts.ops.ci.releasenotes.asString
import flank.scripts.ops.ci.releasenotes.generateReleaseNotes
import flank.scripts.utils.ERROR_WHEN_RUNNING
import flank.scripts.utils.runCommand
import java.io.File
import java.nio.file.Path

fun tryReleaseFlank(path: Path, gitTag: String, commitHash: String, isSnapshotRelease: Boolean, token: String) = when {
    isSnapshotRelease && commitHash.isBlank() -> {
        println("Commit hash is required for snapshot release")
        ERROR_WHEN_RUNNING
    }
    !isSnapshotRelease && token.isBlank() -> {
        println("Git hub token is required for stable release")
        ERROR_WHEN_RUNNING
    }
    else -> releaseFlank(path, gitTag, commitHash, isSnapshotRelease, token)
}

private fun releaseFlank(
    path: Path,
    gitTag: String,
    commitHash: String,
    isSnapshotRelease: Boolean,
    token: String
): Int {
    val releasePath = moveFlankToReleaseDirectory(path)
    val releaseCommand = if (isSnapshotRelease) {
        hubStableSnapshotCommand(releasePath, gitTag, commitHash)
    } else {
        hubStableReleaseCommand(releasePath, gitTag, token)
    }
    return releaseCommand.runCommand()
}

private fun moveFlankToReleaseDirectory(inputPath: Path) =
    if (inputPath.toFile().renameTo(File(RELEASE_DIRECTORY))) RELEASE_DIRECTORY else inputPath.toAbsolutePath()
        .toString()

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
        "-m", generateReleaseNotes(token).asString(gitTag),
        gitTag
    )

private const val RELEASE_DIRECTORY = "./RELEASE/flank.jar"
