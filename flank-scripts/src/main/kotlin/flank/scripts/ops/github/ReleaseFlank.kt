package flank.scripts.ops.github

import com.google.common.annotations.VisibleForTesting
import flank.scripts.ops.common.asString
import flank.scripts.ops.common.generateReleaseNotes
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

@VisibleForTesting
internal fun releaseFlank(
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

/**
 * See more details: https://cli.github.com/manual/gh_release_create
  */
private fun hubStableSnapshotCommand(path: String, gitTag: String, commitHash: String) =
    listOf(
        "gh", "release", "create", gitTag, path,
        "-p", // create a prerelease
        "-t", "Flank $gitTag",
        "-n", "Snapshot release for commit $commitHash",
        "--target", "$commitHash",
    )

private fun hubStableReleaseCommand(path: String, gitTag: String, token: String) =
    listOf(
        "gh", "release", "create", gitTag, path,
        "-t", "Flank $gitTag",
        "-n", generateReleaseNotes(token).asString(gitTag),
    )

private const val RELEASE_DIRECTORY = "./RELEASE/flank.jar"
