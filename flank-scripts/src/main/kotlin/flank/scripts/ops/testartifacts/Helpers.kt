package flank.scripts.ops.testartifacts

import com.jcabi.github.Repo
import flank.common.config.flankTestArtifactsRepository
import flank.scripts.data.github.githubRepo
import flank.scripts.utils.currentGitBranch
import flank.scripts.utils.getEnvOrDefault
import java.io.File

const val GITHUB_TOKEN_ENV_KEY = "GITHUB_TOKEN"
const val FIXTURES_PATH = "test_runner/src/test/kotlin/ftl/fixtures/tmp"
const val TEST_ARTIFACTS_PATH = "test_artifacts"

fun flankRoot() = File(System.getenv("FLANK_ROOT") ?: "../").absoluteFile.normalize()

val File.testArtifacts: File get() = resolve(TEST_ARTIFACTS_PATH).apply { if (!exists()) mkdir() }

fun File.testArtifacts(branch: String = currentGitBranch()): File = testArtifacts.resolve(branch)

internal fun testArtifactsRepo(): Repo =
    githubRepo(getEnvOrDefault(GITHUB_TOKEN_ENV_KEY, ""), flankTestArtifactsRepository)
