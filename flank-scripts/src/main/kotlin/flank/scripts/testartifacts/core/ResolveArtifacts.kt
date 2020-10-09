package flank.scripts.testartifacts.core

import com.jcabi.github.Repo
import java.io.File

fun Context.resolveArtifacts(repo: Repo = testArtifactsRepo()) {
    resolveRemotelyOrFallback(
        repo = repo,
        fallback = { resolveFallback(repo) }
    )
}

private fun Context.resolveRemotelyOrFallback(repo: Repo, fallback: () -> Unit) {
    if (isNewVersionAvailable(repo)) {
        downloadFixtures()
        unzipTestArtifacts()
        linkArtifacts()
    } else fallback()
}

private fun Context.resolveFallback(repo: Repo) {
    projectRoot.testArtifacts(branch).run {
        if (exists()) println("* Resolved test artifacts for branch $branch under $absolutePath")
        else tryResolveForMaster(repo)
    }
}

private fun Context.tryResolveForMaster(repo: Repo) {
    copy(branch = "master").resolveRemotelyOrFallback(
        repo = repo,
        fallback = { projectRoot.testArtifacts(branch).resolveLocallyOrThrow(branch) }
    )
}

private fun File.resolveLocallyOrThrow(branch: String) {
    if (exists()) println("* Resolved test artifacts for branch $branch under $absolutePath")
    else throw Exception("Cannot resolve test artifacts")
}
