package flank.common.config

import flank.common.flankCommonRootPathString
import flank.common.isCI
import java.nio.file.Paths
import java.util.Properties

const val ZENHUB_REPO_ID = "zenhub.repo-id"
const val FLANK_REPO = "repo.flank"
const val GCLOUD_REPO = "repo.gcloud_cli"
const val ARTIFACTS_REPO = "repo.test-artifacts"
const val IT_WORKFLOW_FILE = "integration.workflow-filename"
const val IT_USER = "integration.issue-poster"
const val SDK_WORKFLOW = "sdk-check.workflow-filename"
const val SDK_USER = "sdk-check.issue-poster"

private val defaults = Properties().apply {
    setProperty(ZENHUB_REPO_ID, "84221974")
    setProperty(FLANK_REPO, "Flank/flank")
    setProperty(GCLOUD_REPO, "Flank/gcloud_cli")
    setProperty(ARTIFACTS_REPO, "Flank/test_artifacts")
    setProperty(IT_WORKFLOW_FILE, "full_suite_integration_tests.yml")
    setProperty(IT_USER, "github-actions[bot]")
    setProperty(SDK_WORKFLOW, "update_dependencies_and_client.yml")
    setProperty(SDK_USER, "github-actions[bot]")
}

class SafeProperties(defaults: Properties) : Properties(defaults) {
    override fun get(key: Any?) = (key as String).run {
        requireNotNull(if (shouldUseDefaults()) defaults.getProperty(key) else getProperty(key))
    }
}

// default properties should be used in CI and during tests
private fun shouldUseDefaults() = isCI() || isTest()

private fun isTest() = System.getProperty("testScript") != null

val flankProjectProperties = SafeProperties(defaults).also { prop ->
    with(Paths.get("$flankCommonRootPathString/flank-debug.properties").toFile()) {
        if (exists()) prop.load(inputStream())
    }
}
