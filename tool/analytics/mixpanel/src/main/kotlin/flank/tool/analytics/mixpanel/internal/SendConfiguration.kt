package flank.tool.analytics.mixpanel.internal

import flank.common.toJSONObject
import org.json.JSONObject

private const val PROJECT_ID = "project_id"
private const val NAME_KEY = "name"

fun sendConfiguration(
    project: String,
    events: Map<String, Any?>,
    eventName: String
) =
    project.takeUnless { blockSendUsageStatistics || project.isBlank() }?.run {
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

private fun JSONObject.send() = apiClient.sendMessage(this)

private fun Map<String, Any?>.toEvent(projectId: String, eventName: String): JSONObject =
    (this + Pair(SESSION_ID, sessionId)).run {
        messageBuilder.event(projectId, eventName, toJSONObject())
    }
