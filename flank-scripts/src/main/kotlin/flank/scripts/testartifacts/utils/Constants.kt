package flank.scripts.testartifacts.utils

import flank.scripts.utils.currentGitBranch
import java.io.File

const val GITHUB_TOKEN = "GITHUB_TOKEN"
const val TEST_ARTIFACTS_REPO = "Flank/test_artifacts"

const val RELEASE_LATEST = "latest"

const val FIXTURES_PATH = "test_runner/src/test/kotlin/ftl/fixtures/tmp"
const val TEST_ARTIFACTS_PATH = "test_artifacts"

val String.fixturesPath get() = plus("/$FIXTURES_PATH")

fun flankRoot() = File(System.getenv("FLANK_ROOT") ?: "../")

val File.testArtifacts get() = resolve(TEST_ARTIFACTS_PATH)

fun File.testArtifacts(branch: String = currentGitBranch()) = testArtifacts.resolve(branch)
