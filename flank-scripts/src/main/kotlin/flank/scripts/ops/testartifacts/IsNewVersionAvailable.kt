package flank.scripts.ops.testartifacts

import com.jcabi.github.Repo
import flank.scripts.github.getRelease

internal fun Context.isNewVersionAvailable(
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
