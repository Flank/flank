package flank.common.config

import flank.common.flankCommonRootPathString
import flank.common.isCI
import java.nio.file.Paths
import java.util.Properties

private const val FLANK_REPO = "repo.flank"
private const val GCLOUD_REPO = "repo.gcloud_cli"
private const val ARTIFACTS_REPO = "repo.test-artifacts"
private const val IT_WORKFLOW_FILE = "integration.workflow-filename"
private const val IT_USER = "integration.issue-poster"
private const val SDK_WORKFLOW = "sdk-check.workflow-filename"
private const val SDK_USER = "sdk-check.issue-poster"

private val defaults = Properties().apply {
    setProperty(FLANK_REPO, "Flank/flank")
    setProperty(GCLOUD_REPO, "Flank/gcloud_cli")
    setProperty(ARTIFACTS_REPO, "Flank/test_artifacts")
    setProperty(IT_WORKFLOW_FILE, "integration_tests_pointer.yml")
    setProperty(IT_USER, "github-actions[bot]")
    setProperty(SDK_WORKFLOW, "update_dependencies_pointer.yml")
    setProperty(SDK_USER, "github-actions[bot]")
}

class SafeProperties(defaults: Properties) : Properties(defaults) {
    override fun get(key: Any?) = (key as String).run {
        requireNotNull(if (shouldUseDefaults()) defaults.getProperty(key) else getProperty(key))
    }
}

// default properties should be used in CI and during tests
private fun shouldUseDefaults() = isCI() || isTest()

fun isTest() = System.getProperty("runningTests").toBoolean()

private val props = SafeProperties(defaults).also { prop ->
    with(Paths.get("$flankCommonRootPathString/flank-debug.properties").toFile()) {
        if (exists()) prop.load(inputStream())
    }
}

val flankRepository = props[FLANK_REPO]
val flankGcloudCLIRepository = props[GCLOUD_REPO]
val flankTestArtifactsRepository = props[ARTIFACTS_REPO]
val integrationOpenedIssueUser = props[IT_USER]
val updatesOpenedUser = props[SDK_USER]
val fullSuiteWorkflowFilename = props[IT_WORKFLOW_FILE]
val updateDependenciesWorkflowFilename = props[SDK_WORKFLOW]
