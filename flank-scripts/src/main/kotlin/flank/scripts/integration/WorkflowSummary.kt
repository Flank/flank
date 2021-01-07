package flank.scripts.integration

import flank.scripts.github.commons.getLastWorkflowRunDate

suspend fun getLastITWorkflowRunDate(token: String) = getLastWorkflowRunDate(
    token = token,
    workflowFileName = "integration_tests_pointer.yml"
)
