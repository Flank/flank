package flank.tool.analytics.mixpanel

import flank.common.toJSONObject

private const val PROJECT_ID = "project_id"
private const val NAME_KEY = "name"

fun sendConfiguration(
    project: String,
    events: Map<String, Any?>,
    eventName: String = CONFIGURATION_KEY
) =
    project.takeUnless { blockSendUsageStatistics }?.run {
        registerUser(project)
        events
            .toEvent(project, eventName)
            .send()
    }

private fun registerUser(project: String) {
    messageBuilder.set(
        project, mapOf(PROJECT_ID to project, NAME_KEY to project).toJSONObject()
    ).send()
}
