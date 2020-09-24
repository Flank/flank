package flank.scripts.testartifacts.utils

import com.jcabi.github.Coordinates
import com.jcabi.github.Release
import com.jcabi.github.Releases
import com.jcabi.github.Repo
import com.jcabi.github.RtGithub
import flank.scripts.utils.currentGitBranch

internal fun Repo.getOrCreateArtifactsRelease(
    branch: String = currentGitBranch()
): Release.Smart = releases().getOrCreateRelease(branch)

internal fun Repo.getArtifactsRelease(
    branch: String = currentGitBranch()
): Release.Smart? = releases()
    .let(Releases::Smart)
    .find(branch)
    ?.let(Release::Smart)

fun testArtifactsRepo(): Repo =
    RtGithub(getEnv(GITHUB_TOKEN))
        .repos()
        .get(Coordinates.Simple(TEST_ARTIFACTS_REPO))

private fun getEnv(key: String): String =
    System.getenv(key) ?: throw RuntimeException("Environment variable '$key' not found!")

private fun Releases.getOrCreateRelease(tag: String) =
    Release.Smart(
        try {
            print("* Fetching release $tag - ")
            Releases.Smart(this).find(tag).also { println("OK") }
        } catch (e: IllegalArgumentException) {
            println("FAILED")
            print("* Creating new release $tag - ")
            create(tag).also { println("OK") }
        }
    )
