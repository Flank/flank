package flank.scripts.testartifacts.core

import com.jcabi.github.Repo
import flank.scripts.github.githubRepo

internal fun testArtifactsRepo(): Repo = githubRepo(GITHUB_TOKEN, TEST_ARTIFACTS_REPO)
