package flank.scripts.release.hub

import flank.scripts.utils.runCommand
import java.io.File
import java.nio.file.Path

fun releaseFlank(path: Path, gitTag: String, commitHash: String, isSnapshotRelease: Boolean): Int {
    val releasePath = moveFlankToReleaseDirectory(path)
    val releaseCommand = if (isSnapshotRelease) {
        hubStableSnapshotCommand(releasePath, gitTag, commitHash)
    } else {
        hubStableReleaseCommand(releasePath, gitTag, commitHash)
    }
    return releaseCommand.runCommand()
}

private fun hubStableReleaseCommand(path: String, gitTag: String, commitHash: String) =
        "hub release create -a $path -m 'Flank $gitTag' -m 'Stable release for commit $commitHash' $gitTag".also {
            println(it)
        }
private fun hubStableSnapshotCommand(path: String, gitTag: String, commitHash: String) =
        "hub release create -p -a $path -m 'Flank $gitTag' -m 'Snapshot release for commit $commitHash' $gitTag".also {
            println(it)
        }

private fun moveFlankToReleaseDirectory(inputPath: Path) =
        if (inputPath.toFile().renameTo(File(RELEASE_DIRECTORY))) RELEASE_DIRECTORY else inputPath.toAbsolutePath().toString()

private const val RELEASE_DIRECTORY = "./RELEASE/flank.jar"
