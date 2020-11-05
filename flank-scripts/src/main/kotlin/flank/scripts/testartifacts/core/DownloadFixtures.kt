package flank.scripts.testartifacts.core

import com.jcabi.github.Release
import flank.scripts.github.getRelease
import flank.scripts.utils.downloadFile
import java.io.File

fun Context.downloadFixtures(
    overwrite: Boolean = false
) {
    downloadFixtures(
        url = latestArtifactZipUrl(branch),
        testArtifacts = projectRoot.testArtifacts,
        overwrite = overwrite
    )
}

private fun Context.downloadFixtures(
    testArtifacts: File,
    url: String,
    overwrite: Boolean
) {
    print("* Downloading test artifacts from url $url - ")
    testArtifacts.resolve(
        ArtifactsArchive(
            branch = branch,
            timestamp = url.parseArtifactsArchive().timestamp
        ).fullName
    ).run {
        if (exists() && overwrite) delete()
        if (!exists()) downloadFile(url, absolutePath).also { println("OK") }
        else println("ABORTED (already exists)")
    }
}

private fun latestArtifactZipUrl(branch: String): String =
    requireNotNull(
        testArtifactsRepo().getRelease(branch)
    ) { "Cannot find release for branch $branch" }.run {
        requireNotNull(
            latestArtifactZipUrl()
        ) { "Cannot find artifacts download url for release ${name()}" }
    }

private fun Release.Smart.latestArtifactZipUrl() = assets()
    .iterate()
    .map { it.json() }
    .filter { zipFileRegex.matches(it.getString("name")) }
    .maxByOrNull { it.getString("name").toString() }
    ?.getString("browser_download_url")

private val zipFileRegex = Regex("^\\d+\\.zip")
