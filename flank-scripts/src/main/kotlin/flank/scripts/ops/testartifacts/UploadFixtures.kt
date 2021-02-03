package flank.scripts.ops.testartifacts

import com.jcabi.github.Release
import com.jcabi.github.ReleaseAsset
import flank.scripts.data.github.getOrCreateRelease
import java.io.File

fun Context.uploadFixtures() {
    uploadFixtures(
        release = testArtifactsRepo()
            .getOrCreateRelease(branch),
        zipFile = requireNotNull(
            latestArtifactsArchive()
        ) { "Nothing to upload" }
    )
}

private fun Context.uploadFixtures(
    release: Release.Smart,
    zipFile: File,
    archive: ArtifactsArchive = zipFile.name.parseArtifactsArchive()
) = release.run {
    print("* Removing assets from ${url()} - ")
    removeAssets()
    println("OK")
    print("* Uploading fixtures $zipFile - ")
    System.out.flush()
    assets().upload(
        zipFile.readBytes(),
        "application/zip",
        archive.shortName
    )
    name(tag())
    body(archive.timestamp.toString())
    println("OK")
}

private fun Release.removeAssets() = assets().iterate().forEach(ReleaseAsset::remove)
