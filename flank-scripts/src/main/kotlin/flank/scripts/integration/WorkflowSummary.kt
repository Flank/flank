package flank.scripts.integration

import flank.scripts.github.commons.getLastWorkflowRunDate

suspend fun getLastITWorkflowRunDate(token: String) = getLastWorkflowRunDate(
    token = token,
    workflowFileName = "full_suite_integration_tests.yml"
)
