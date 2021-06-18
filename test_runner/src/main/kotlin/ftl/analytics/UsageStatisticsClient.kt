package ftl.analytics

import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.google.common.annotations.VisibleForTesting
import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI
import ftl.util.SESSION_ID
import ftl.util.sessionId
import org.json.JSONObject

private const val MIXPANEL_API_TOKEN = "d9728b2c8e6ca9fd6de1fcd32dd8cdc2"

internal val messageBuilder by lazy {
    MessageBuilder(MIXPANEL_API_TOKEN)
}

internal val apiClient by lazy {
    MixpanelAPI()
}

internal val objectMapper by lazy {
    jsonMapper {
        addModule(kotlinModule())
    }
}

@VisibleForTesting
internal fun JSONObject.send() = apiClient.sendMessage(this)

internal fun Map<String, Any?>.toEvent(projectId: String, eventName: String) =
    (this + Pair(SESSION_ID, sessionId)).run {
        messageBuilder.event(projectId, eventName, toJSONObject())
    }
