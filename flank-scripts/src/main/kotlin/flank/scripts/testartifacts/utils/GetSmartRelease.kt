package flank.scripts.testartifacts.utils

import com.jcabi.github.Coordinates
import com.jcabi.github.Release
import com.jcabi.github.Releases
import com.jcabi.github.Repo
import com.jcabi.github.RtGithub
import java.io.IOException

internal fun getOrCreateArtifactsRelease(
    name: String
): Release.Smart =
    getTestArtifactsRepo()
        .releases()
        .getOrCreateRelease(name)

private fun getTestArtifactsRepo(): Repo =
    RtGithub(getEnv(GITHUB_TOKEN))
        .repos()
        .get(Coordinates.Simple(TEST_ARTIFACTS_REPO))

private fun getEnv(key: String): String =
    System.getenv(key) ?: throw RuntimeException("Environment variable '$key' not found!")

private fun Releases.getOrCreateRelease(name: String) =
    Release.Smart(
        try {
            Releases.Smart(this).find(name)
        } catch (e: IOException) {
            create(name)
        }
    )
