package flank.tool.analytics

import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI
import org.json.JSONObject
import kotlin.reflect.KClass

private const val MIXPANEL_API_TOKEN = "d9728b2c8e6ca9fd6de1fcd32dd8cdc2"

var blockSendUsageStatistics: Boolean = false

internal val messageBuilder by lazy {
    MessageBuilder(MIXPANEL_API_TOKEN)
}

internal val apiClient by lazy {
    MixpanelAPI()
}

val objectMapper by lazy {
    jsonMapper {
        addModule(kotlinModule())
    }
}

fun initUsageStatistics(sendUsageStatistics: Boolean, vararg statisticClasses: KClass<*>) {
    blockSendUsageStatistics = sendUsageStatistics.not()
    classesForStatistics = statisticClasses.asList()
}

fun JSONObject.send() = apiClient.sendMessage(this)

fun Map<String, Any?>.toEvent(projectId: String, eventName: String): JSONObject =
    (this + Pair(SESSION_ID, sessionId)).run {
        messageBuilder.event(projectId, eventName, toJSONObject())
    }
