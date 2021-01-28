package flank.scripts.ops.shell.firebase.sdk

import flank.common.config.updateDependenciesWorkflowFilename
import flank.scripts.data.github.commons.getLastWorkflowRunDate

suspend fun getLastSDKUpdateRunDate(token: String) = getLastWorkflowRunDate(
    token = token,
    workflowFileName = updateDependenciesWorkflowFilename
)
