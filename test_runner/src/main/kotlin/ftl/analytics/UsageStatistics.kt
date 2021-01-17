package ftl.analytics

import com.fasterxml.jackson.core.type.TypeReference
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import org.json.JSONObject
import kotlin.reflect.KClass

private const val PROJECT_ID = "project_id"
private const val COMMON_ARGS = "commonArgs"
private const val NAME_KEY = "name"
private const val COVERAGE_FILE_PATH = "coverageFilePath"
private const val ENVIRONMENT_VARIABLES = "environmentVariables"
private const val ANONYMIZE_VALUE = "..."

private val keysToRemove by lazy {
    IArgs::class.findMembersWithAnnotation(IgnoreInStatistics::class) +
        AndroidArgs::class.findMembersWithAnnotation(IgnoreInStatistics::class) +
        IosArgs::class.findMembersWithAnnotation(IgnoreInStatistics::class)
}

private val keysToAnonymize by lazy {
    IArgs::class.findMembersWithAnnotation(AnonymizeInStatistics::class) +
        AndroidArgs::class.findMembersWithAnnotation(AnonymizeInStatistics::class) +
        IosArgs::class.findMembersWithAnnotation(AnonymizeInStatistics::class)
}

private fun KClass<*>.findMembersWithAnnotation(annotationType: KClass<*>) = this.members.filter {
    it.annotations.any { annotation -> annotation.annotationClass == annotationType }
}.map {
    it.name
}

internal fun IArgs.registerUser(): IArgs = apply {
    messageBuilder.set(
        project, mapOf(PROJECT_ID to project, NAME_KEY to project).toJSONObject()
    ).sendMessage()
}

internal fun Any.objectToMap() = objectMapper.convertValue(this, object : TypeReference<Map<String, Any>>() {})

internal fun Map<String, Any>.filterNonCommonArgs() = filter { it.key != COMMON_ARGS }

internal fun Map<String, Any>.getNonDefaultArgs(defaultArgs: Map<String, Any>) =
    keys.fold(mapOf<String, Any?>()) { acc, key ->
        acc.compareValues(key, this, defaultArgs[key])
    }

@Suppress("UNCHECKED_CAST")
private fun Map<String, Any?>.compareValues(key: String, source: Map<String, Any>, defaultValue: Any?) =
    keysToAnonymize.let { keysToAnonymize ->
        when {
            (key == ENVIRONMENT_VARIABLES) -> this + (source[key] as Map<String, String>).filterSensitiveDataInEnv()
            source[key] == defaultValue || key in keysToRemove -> this
            keysToAnonymize.contains(key) -> this + (key to source[key]?.toAnonymous())
            else -> this + (key to source[key])
        }
    }

private fun Map<String, String>.filterSensitiveDataInEnv() = keys.fold(
    mapOf<String, String>(),
    { acc, key ->
        acc +
            if (key == COVERAGE_FILE_PATH) (key to ANONYMIZE_VALUE)
            else (key to (this[key].orEmpty()))
    }
)

private fun Any.toAnonymous() = when (this) {
    is List<*> -> "Count: $size"
    else -> ANONYMIZE_VALUE
}

private fun Map<*, *>.toJSONObject() = JSONObject(this)
