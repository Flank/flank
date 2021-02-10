package flank.scripts.ops.firebase

import flank.common.config.updateDependenciesWorkflowFilename
import flank.scripts.data.github.commons.getLastWorkflowRunDate

suspend fun getLastSDKUpdateRunDate(token: String) = getLastWorkflowRunDate(
    token = token,
    workflowFileName = updateDependenciesWorkflowFilename
)
