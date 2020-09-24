package flank.scripts.testartifacts.utils

import com.jcabi.github.Repo
import flank.scripts.utils.currentGitBranch
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

internal fun artifactsOutdated(
    repo: Repo = testArtifactsRepo(),
    projectRoot: File = flankRoot(),
    branch: String = currentGitBranch()
): Boolean {

    val remote = repo.getArtifactsRelease(branch)
        ?.body()?.toLong()
        ?: return false

    val local = latestArtifactsZip(projectRoot, branch)
        ?.name?.parseTestArtifactsZip()?.timestamp
        ?: return true

    return remote > local
}
