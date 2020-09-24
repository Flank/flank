package flank.scripts.testartifacts.utils

import com.jcabi.github.Release
import flank.scripts.utils.md5
import java.io.File

internal fun Release.Smart.uploadFixtures(
    name: String,
    zipFile: String
) {
    val file = File(zipFile).assertZipName()
    val bytes = file.readBytes()
    val md5 = bytes.md5()
    removeAssets()
    assets().upload(bytes, "application/zip", file.name)
    body(md5)
    name(name)
    body("Test artifacts for Flank branch $name.")
}

private fun File.assertZipName() = apply {
    require(zipRegex.matches(name)) { "$name must matches ${zipRegex.pattern}" }
}

private val zipRegex = Regex("^.*-\\d*\\.zip")

private fun Release.removeAssets() {
    assets().iterate().forEach { asset ->
        println("Removing $asset")
        asset.remove()
    }
}
