package flank.scripts.shell.firebase.sdk

import flank.scripts.github.commons.getLastWorkflowRunDate

suspend fun getLastSDKUpdateRunDate(token: String) = getLastWorkflowRunDate(
    token = token,
    workflowFileName = "update_dependencies_and_client.yml"
)
