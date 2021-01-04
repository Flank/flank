package flank.scripts.integration

import flank.scripts.config.fullSuiteWorkflowFilename
import flank.scripts.github.commons.getLastWorkflowRunDate

suspend fun getLastITWorkflowRunDate(token: String) = getLastWorkflowRunDate(
    token = token,
    workflowFileName = fullSuiteWorkflowFilename
)
