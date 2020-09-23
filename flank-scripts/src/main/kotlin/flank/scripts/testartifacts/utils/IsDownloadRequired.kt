package flank.scripts.testartifacts.utils

import org.jsoup.nodes.Element
import java.io.File

internal fun Element.isDownloadRequired(
    fixturesPath: String = FIXTURES_PATH
): Boolean {
    val remoteAssetName = attr("href").split("/").last()
    return File(fixturesPath).run {
        if (!exists()) true
        else listFiles()
            ?.none { it.name == remoteAssetName }
            ?: true
    }
}
