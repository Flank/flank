package flank.scripts.testartifacts.core

import flank.scripts.utils.currentGitBranch
import java.io.File

data class ArtifactsArchive(
    val branch: String = currentGitBranch(),
    val timestamp: Long = System.currentTimeMillis()
) {
    val fullName get() = "$branch-$timestamp.$ext"
    val shortName get() = "$$timestamp.$ext"

    override fun toString(): String = fullName

    companion object {
        const val ext = "zip"
    }
}

fun String.parseArtifactsArchive(
    branch: String? = null
): ArtifactsArchive = removeSuffix(".zip")
    .reversed()
    .split("-", "/", limit = 2)
    .run {
        ArtifactsArchive(
            branch = branch ?: last().reversed(),
            timestamp = first().reversed().toLong()
        )
    }

fun Context.latestArtifactsArchive(): File? = artifactArchiveRegex(branch).let { regex ->
    projectRoot.testArtifacts.listFiles { file -> regex.matches(file.name) }
        ?.maxByOrNull { archiveNameTimestamp(it.name) }
}

private fun artifactArchiveRegex(branch: String = currentGitBranch()) = Regex("^$branch-\\d*.${ArtifactsArchive.ext}")

private fun archiveNameTimestamp(name: String) = name.split("-").last().split(".").first()
