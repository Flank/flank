package flank.scripts.testartifacts.utils

import com.jcabi.github.Release
import flank.scripts.utils.currentGitBranch
import flank.scripts.utils.md5
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
//    val bytes =
//    val md5 = bytes.md5()
    print("* Removing assets from ${url()} - ")
    removeAssets()
    println("OK")
    print("* Uploading fixtures $zipFile - ")
    assets().upload(zipFile.assertZipName().readBytes(), "application/zip", zipFile.name)
//    body(md5)
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
        println("Removing $asset")
        asset.remove()
    }
}
