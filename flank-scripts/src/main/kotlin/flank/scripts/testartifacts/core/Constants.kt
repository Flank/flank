package flank.scripts.testartifacts.core

import flank.scripts.utils.currentGitBranch
import java.io.File

const val GITHUB_TOKEN = "GITHUB_TOKEN"
const val TEST_ARTIFACTS_REPO = "Flank/test_artifacts"

const val FIXTURES_PATH = "test_runner/src/test/kotlin/ftl/fixtures/tmp"
const val TEST_ARTIFACTS_PATH = "test_artifacts"

fun flankRoot() = File(System.getenv("FLANK_ROOT") ?: "../").absoluteFile.normalize()

val File.testArtifacts: File get() = resolve(TEST_ARTIFACTS_PATH).apply { if (!exists()) mkdir() }

fun File.testArtifacts(branch: String = currentGitBranch()): File = testArtifacts.resolve(branch)
