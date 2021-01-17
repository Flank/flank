package ftl.analytics

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.args.blockSendingUsageStatistics
import org.json.JSONObject

private const val MIXPANEL_API_TOKEN = "d9728b2c8e6ca9fd6de1fcd32dd8cdc2"
private const val CONFIGURATION_KEY = "configuration"
private const val PROJECT_ID = "project_id"
private const val COMMON_ARGS = "commonArgs"
private const val NAME_KEY = "name"

fun AndroidArgs.sendConfiguration() = takeUnless { blockSendingUsageStatistics }?.let {
    registerUser()
    AndroidArgs.default().let { defaultArgs ->
        objectToMap().filterNonCommonArgs().getNonDefaultArgs(defaultArgs.objectToMap())
            .plus(commonArgs.objectToMap().getNonDefaultArgs(defaultArgs.commonArgs.objectToMap()))
            .createEvent(project)
            .sendMessage()
    }
}

fun IosArgs.sendConfiguration() = takeUnless { blockSendingUsageStatistics }?.let {
    registerUser()
    IosArgs.default().let { defaultArgs ->
        objectToMap().filterNonCommonArgs().getNonDefaultArgs(defaultArgs.objectToMap())
            .plus(commonArgs.objectToMap().getNonDefaultArgs(defaultArgs.commonArgs.objectToMap()))
            .createEvent(project)
            .sendMessage()
    }
}

private fun Map<String, Any>.filterNonCommonArgs() = filter { it.key != COMMON_ARGS }

private fun IArgs.registerUser(): IArgs = apply {
    messageBuilder.set(
        project,
        JSONObject(
            mapOf(
                PROJECT_ID to project,
                NAME_KEY to project
            )
        )
    ).sendMessage()
}

private fun JSONObject.sendMessage() = apiClient.sendMessage(this)

private fun Map<String, Any?>.createEvent(projectId: String) =
    messageBuilder.event(projectId, CONFIGURATION_KEY, JSONObject(this))

private fun Any.objectToMap() = objectMapper.convertValue(this, object : TypeReference<Map<String, Any>>() {})

private fun Map<String, Any>.getNonDefaultArgs(defaultArgs: Map<String, Any>) =
    keys.fold(mapOf<String, Any?>()) { acc, key ->
        acc.compareValues(key, this, defaultArgs[key])
    }

private fun Map<String, Any?>.compareValues(key: String, source: Map<String, Any>, defaultValue: Any?) =
    if (source[key] != defaultValue) this + (key to source[key])
    else this

private val messageBuilder by lazy {
    MessageBuilder(MIXPANEL_API_TOKEN)
}

private val apiClient by lazy {
    MixpanelAPI()
}

private val objectMapper by lazy {
    jsonMapper {
        addModule(kotlinModule())
    }
}
