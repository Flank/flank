package flank.scripts.testartifacts.utils

import flank.scripts.utils.download
import flank.scripts.utils.isOnline
import flank.scripts.utils.unzip
import okhttp3.OkHttpClient
import org.jsoup.nodes.Element
import java.io.File

fun downloadFixtures() {
    if (isOnline()) fetchRemoteAssetLink().run {
        if (isDownloadRequired()) downloadFixtures()
    }
}

private fun Element.downloadFixtures(
    downloadPath: String
) {
    val fixtures = File(downloadPath)
    if (fixtures.exists()) fixtures.deleteRecursively()
    fixtures.mkdirs()

    val downloadUrl = "https://github.com${attr("href")}"
    val assetName = attr("href").split("/").last()
    val zipPath = "${fixtures.path}/$assetName"
    OkHttpClient().download(downloadUrl, zipPath)

    unzip(File(zipPath), fixtures)
}
