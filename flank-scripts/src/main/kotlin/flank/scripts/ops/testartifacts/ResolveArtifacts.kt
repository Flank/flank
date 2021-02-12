package flank.scripts.ops.testartifacts

import com.jcabi.github.Repo
import flank.scripts.data.github.getRelease

fun Context.resolveArtifacts(
    repo: Repo = testArtifactsRepo()
) {
    if (isNewVersionAvailable(repo)) {
        downloadFixtures()
        unzipTestArtifacts()
        linkArtifacts()
    } else projectRoot.testArtifacts(branch).run {
        if (exists()) println("* Resolved test artifacts for branch $branch under $absolutePath")
        else copy(branch = "master").run {
            if (isNewVersionAvailable(repo)) {
                downloadFixtures()
                unzipTestArtifacts()
                linkArtifacts()
            } else projectRoot.testArtifacts(branch).run {
                if (exists()) println("* Resolved test artifacts for branch $branch under $absolutePath")
                else throw Exception("Cannot resolve test artifacts")
            }
        }
    }
}

private fun Context.isNewVersionAvailable(
    repo: Repo = testArtifactsRepo()
): Boolean {
    val remote = repo.getRelease(branch)
        ?.body()?.toLong()
        ?: return false

    val local = latestArtifactsArchive()
        ?.name?.parseArtifactsArchive()?.timestamp
        ?: return true

    return remote > local
}
