package flank.scripts.ops.integration

import flank.common.config.fullSuiteWorkflowFilename
import flank.scripts.github.commons.getLastWorkflowRunDate

internal suspend fun getLastITWorkflowRunDate(token: String) = getLastWorkflowRunDate(
    token = token,
    workflowFileName = fullSuiteWorkflowFilename
)
