package flank.scripts.testartifacts.utils

import com.jcabi.github.Release
import flank.scripts.utils.currentGitBranch
import java.io.File

fun uploadFixtures(
    projectRoot: File = flankRoot(),
    branch: String = currentGitBranch()
) {
    testArtifactsRepo().getOrCreateArtifactsRelease(branch).uploadFixtures(
        zipFile = requireNotNull(
            latestArtifactsZip(
                projectRoot = projectRoot,
                branch = branch
            )
        ) { "Nothing to upload" }
    )
}

private fun Release.Smart.uploadFixtures(
    zipFile: File
) {
    print("* Removing assets from ${url()} - ")
    removeAssets()
    println("OK")
    print("* Uploading fixtures $zipFile - ")
    System.out.flush()
    assets().upload(
        zipFile.assertZipName().readBytes(),
        "application/zip",
        zipFile.name.parseTestArtifactsZip().shortName
    )
    name(tag())
    body("Test artifacts for Flank branch ${tag()}.")
    println("OK")
}

private fun File.assertZipName() = apply {
    require(exists()) { "File $absolutePath not exit" }
    require(zipRegex.matches(name)) { "$name must matches ${zipRegex.pattern}" }
}

private val zipRegex = Regex("^.*-\\d*\\.zip")

private fun Release.removeAssets() {
    assets().iterate().forEach { asset ->
        asset.remove()
    }
}
