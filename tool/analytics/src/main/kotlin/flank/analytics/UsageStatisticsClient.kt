package flank.analytics

import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI
import flank.common.toJSONObject
import org.json.JSONObject
import java.util.UUID

private const val MIXPANEL_API_TOKEN = "d9728b2c8e6ca9fd6de1fcd32dd8cdc2"
private const val SESSION_ID = "session.id"
private const val PROJECT_ID = "project_id"
private const val NAME_KEY = "name"

internal val messageBuilder by lazy {
    MessageBuilder(MIXPANEL_API_TOKEN)
}

private val apiClient by lazy {
    MixpanelAPI()
}

fun JSONObject.send() = apiClient.sendMessage(this)

fun Map<String, Any?>.toEvent(projectId: String, eventName: String): JSONObject =
    messageBuilder.event(projectId, eventName, (this + sessionIdField).toJSONObject())

val sessionId by lazy {
    UUID.randomUUID().toString()
}

fun registerUser(userKey: String) {
    messageBuilder.set(
        userKey, mapOf(PROJECT_ID to userKey, NAME_KEY to userKey).toJSONObject()
    ).send()
}

private val sessionIdField = Pair(SESSION_ID, sessionId)
