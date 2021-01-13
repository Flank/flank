package ftl.analytics

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import org.json.JSONObject

private const val MIXPANEL_API_TOKEN = "f7490466a203188ecf591b9e7b0ae19d"
private const val CONFIGURATION_KEY = "configuration"
private const val PROJECT_ID = "project_id"
private const val COMMON_ARGS = "commonArgs"
private const val NAME_KEY = "name"

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

private fun IArgs.registerUser() {
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

fun AndroidArgs.sendConfiguration() {
    registerUser()
    val defaultArgs = AndroidArgs.default()
    val defaultArgsMap = defaultArgs.objectToMap()
    val defaultCommonArgs = defaultArgs.commonArgs.objectToMap()

    objectToMap().filter { it.key != COMMON_ARGS }.getNonDefaultArgs(defaultArgsMap)
        .plus(commonArgs.objectToMap().getNonDefaultArgs(defaultCommonArgs))
        .let {
            it.forEach {
                messageBuilder.event(
                    project,
                    it.key, JSONObject(mapOf(it.key to it.value))
                ).sendMessage()
            }
        }
}

fun IosArgs.sendConfiguration() {
    registerUser()
    val defaultArgs = IosArgs.default()
    val defaultArgsMap = defaultArgs.objectToMap()
    val defaultCommonArgs = defaultArgs.commonArgs.objectToMap()

    objectToMap().filter { it.key != COMMON_ARGS }.getNonDefaultArgs(defaultArgsMap)
        .plus(commonArgs.objectToMap().getNonDefaultArgs(defaultCommonArgs))
        .let {
            it.forEach {
                messageBuilder.event(
                    project,
                    it.key, JSONObject(mapOf(it.key to it.value))
                ).sendMessage()
            }
        }
}

private fun Map<String, Any>.getNonDefaultArgs(defaultArgs: Map<String, Any>) =
    keys.fold(mapOf<String, Any?>()) { acc, key ->
        acc.compareValues(key, this, defaultArgs[key])
    }

private fun Map<String, Any?>.compareValues(key: String, source: Map<String, Any>, defaultValue: Any?) =
    if (source[key] != defaultValue) this + (key to source[key])
    else this

private fun Any.objectToMap() = objectMapper.convertValue(this, object : TypeReference<Map<String, Any>>() {})
