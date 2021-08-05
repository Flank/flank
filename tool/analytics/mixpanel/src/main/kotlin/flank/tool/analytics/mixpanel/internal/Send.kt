package flank.tool.analytics.mixpanel.internal

import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI
import flank.tool.analytics.mixpanel.Mixpanel.PROJECT_ID
import flank.tool.analytics.mixpanel.Mixpanel.PROJECT_NAME
import flank.tool.analytics.mixpanel.Mixpanel.SESSION_ID
import flank.tool.analytics.mixpanel.Mixpanel.sessionId
import org.json.JSONObject

internal fun sendReport(
    eventName: String,
) {
    !Report.blockSendUsageStatistics || return
    Report.projectName.isNotBlank() || return
    listOf(
        createUser(Report.projectName),
        createEvent(Report.projectName, eventName, Report.data),
    ).forEach(apiClient::sendMessage)
}

private fun createUser(
    project: String
): JSONObject =
    messageBuilder.set(
        project,
        JSONObject(
            mapOf(
                PROJECT_ID to project,
                PROJECT_NAME to project
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
        JSONObject(data + (SESSION_ID to sessionId))
    )

private val messageBuilder by lazy { MessageBuilder(MIXPANEL_API_TOKEN) }
private val apiClient by lazy { MixpanelAPI() }

private const val MIXPANEL_API_TOKEN = "d9728b2c8e6ca9fd6de1fcd32dd8cdc2"
