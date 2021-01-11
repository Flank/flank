package flank.scripts.shell.firebase.sdk

import flank.common.config.updateDependenciesWorkflowFilename
import flank.scripts.github.commons.getLastWorkflowRunDate

suspend fun getLastSDKUpdateRunDate(token: String) = getLastWorkflowRunDate(
    token = token,
    workflowFileName = updateDependenciesWorkflowFilename
)
