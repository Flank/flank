package flank.scripts.testartifacts.core

import com.jcabi.github.Release
import com.jcabi.github.ReleaseAsset
import flank.scripts.github.getOrCreateRelease
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
    assetName: String = zipFile.name.parseArtifactsArchive().shortName
) = release.run {
    print("* Removing assets from ${url()} - ")
    removeAssets()
    println("OK")
    print("* Uploading fixtures $zipFile - ")
    System.out.flush()
    assets().upload(
        zipFile.readBytes(),
        "application/zip",
        assetName
    )
    name(tag())
    body("Test artifacts for Flank branch ${tag()}.")
    println("OK")
}

private fun Release.removeAssets() = assets().iterate().forEach(ReleaseAsset::remove)
