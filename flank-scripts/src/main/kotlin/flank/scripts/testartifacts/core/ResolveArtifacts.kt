package flank.scripts.testartifacts.core

import com.jcabi.github.Repo

fun Context.resolveArtifacts(
    repo: Repo = testArtifactsRepo()
) {
    if (isNewVersionAvailable(repo)) {
        downloadFixtures()
        unzipTestArtifacts()
        linkArtifacts()
    } else if (!projectRoot.testArtifacts(branch).exists())
        copy(branch = "master").run {
            downloadFixtures()
            unzipTestArtifacts()
            linkArtifacts()
        }
}
