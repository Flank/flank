package flank.tool.analytics.mixpanel.internal

import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI
import flank.tool.analytics.mixpanel.Mixpanel
import org.json.JSONObject

internal fun sendConfiguration(
    project: String,
    events: Map<String, Any?>,
    eventName: String
) {
    !AnalyticsReport.blockSendUsageStatistics || return
    project.isNotBlank() || return
    listOf(
        createUser(project),
        createEvent(project, eventName, events),
    ).forEach(apiClient::sendMessage)
}

private fun createUser(
    project: String
): JSONObject =
    messageBuilder.set(
        project,
        JSONObject(
            mapOf(
                Mixpanel.PROJECT_ID to project,
                Mixpanel.PROJECT_NAME to project
            )
        )
    )

private fun createEvent(
    projectId: String,
    eventName: String,
    data: Map<String, Any?>
): JSONObject =
    messageBuilder.event(
        projectId,
        eventName,
        JSONObject(data.plus(Mixpanel.SESSION_ID to Mixpanel.sessionId))
    )

internal val messageBuilder by lazy { MessageBuilder(MIXPANEL_API_TOKEN) }

internal val apiClient by lazy { MixpanelAPI() }

private const val MIXPANEL_API_TOKEN = "d9728b2c8e6ca9fd6de1fcd32dd8cdc2"


