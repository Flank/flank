package flank.scripts.config

import flank.common.config.ARTIFACTS_REPO
import flank.common.config.FLANK_REPO
import flank.common.config.GCLOUD_REPO
import flank.common.config.IT_USER
import flank.common.config.IT_WORKFLOW_FILE
import flank.common.config.SDK_USER
import flank.common.config.SDK_WORKFLOW
import flank.common.config.ZENHUB_REPO_ID
import flank.common.config.flankProjectProperties

private val props = flankProjectProperties

val zenhubRepositoryID = Integer.parseInt(props[ZENHUB_REPO_ID])
val flankRepository = props[FLANK_REPO]
val flankGcloudCLIRepository = props[GCLOUD_REPO]
val flankTestArtifactsRepository = props[ARTIFACTS_REPO]
val integrationOpenedIssueUser = props[IT_USER]
val updatesOpenedUser = props[SDK_USER]
val fullSuiteWorkflowFilename = props[IT_WORKFLOW_FILE]
val updateDependenciesWorkflowFilename = props[SDK_WORKFLOW]
