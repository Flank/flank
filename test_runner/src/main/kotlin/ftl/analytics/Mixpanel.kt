package ftl.analytics

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI
import ftl.args.AndroidArgs
import ftl.args.IosArgs
import org.json.JSONObject

private const val MIXPANEL_API_TOKEN = "f7490466a203188ecf591b9e7b0ae19d"
private const val CONFIGURATION_KEY = "configuration"

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

fun registerUser(projectId: String) {
    messageBuilder.set(
        projectId,
        JSONObject("PROJECT_ID" to projectId)
    ).sendMessage()
}

private fun JSONObject.sendMessage() = apiClient.sendMessage(this)

fun AndroidArgs.sendConfiguration() {
    val defaultArgs = AndroidArgs.default()
    val defaultArgsMap = defaultArgs.objectToMap()
    val defaultCommonArgs = defaultArgs.commonArgs.objectToMap()

    objectToMap().filter { it.key != "commonArgs" }.getNonDefaultArgs(defaultArgsMap)
        .plus(commonArgs.objectToMap().getNonDefaultArgs(defaultCommonArgs))
        .let {
            messageBuilder.event(project, CONFIGURATION_KEY, JSONObject(it))
        }
}

fun IosArgs.sendConfiguration() {
    val defaultArgs = IosArgs.default()
    val defaultArgsMap = defaultArgs.objectToMap()
    val defaultCommonArgs = defaultArgs.commonArgs.objectToMap()

    objectToMap().filter { it.key != "commonArgs" }.getNonDefaultArgs(defaultArgsMap)
        .plus(commonArgs.objectToMap().getNonDefaultArgs(defaultCommonArgs))
        .let {
            messageBuilder.event(project, CONFIGURATION_KEY, JSONObject(it))
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
