package flank.scripts.testartifacts.core

import com.jcabi.github.Repo

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
