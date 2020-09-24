package flank.scripts.testartifacts.utils

import flank.scripts.utils.currentGitBranch
import java.io.File

const val GITHUB_TOKEN = "GITHUB_TOKEN"
const val FIXTURES_PATH = "test_runner/src/test/kotlin/ftl/fixtures/tmp"
const val RELEASE_LATEST = "latest"
const val TEST_ARTIFACTS_REPO = "Flank/test_artifacts"


val String.fixturesPath get() = plus("/$FIXTURES_PATH")

fun String.testArtifactsPath(branchName: String = currentGitBranch()) =
    plus("test_artifacts/$branchName")

fun flankRoot() = File(System.getenv("FLANK_ROOT"))
