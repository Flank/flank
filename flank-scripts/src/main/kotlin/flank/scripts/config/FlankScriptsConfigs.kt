package flank.scripts.config

import flank.scripts.shell.utils.flankScriptsRootPathString
import java.nio.file.Paths
import java.util.Properties

private const val ZENHUB_REPO_ID = "zenhub.repo-id"
private const val FLANK_REPO = "repo.flank"
private const val GCLOUD_REPO = "repo.gcloud_cli"
private const val ARTIFACTS_REPO = "repo.test-artifacts"
private const val IT_WORKFLOW_FILE = "integration.workflow-filename"
private const val IT_USER = "integration.issue-poster"
private const val SDK_WORKFLOW = "sdk-check.workflow-filename"
private const val SDK_USER = "sdk-check.issue-poster"

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

private class SafeProperties(defaults: Properties) : Properties(defaults) {
    override fun get(key: Any?) = (key as String).run {
        requireNotNull(
            if (
                // we want our CI to use defaults always 
                System.getenv("CI") != null ||
                // we don't want to use developers' properties during tests
                System.getProperty("testScript") != null
            ) defaults.getProperty(key)
            else getProperty(key)
        )
    }
}

private val props = SafeProperties(defaults).also { prop ->
    with(Paths.get("$flankScriptsRootPathString/flank-scripts.properties").toFile()) {
        if (exists()) prop.load(inputStream())
    }
}

val zenhubFlankRepoID = Integer.parseInt(props[ZENHUB_REPO_ID])
val flankRepo = props[FLANK_REPO]
val flankGcloudCLIRepo = props[GCLOUD_REPO]
val flankTestArtifactsRepo = props[ARTIFACTS_REPO]
val integrationOpenedIssueUser = props[IT_USER]
val updatesOpenedUser = props[SDK_USER]
val fullSuiteWorkflowFilename = props[IT_WORKFLOW_FILE]
val updateDependenciesWorkflowFilename = props[SDK_WORKFLOW]
