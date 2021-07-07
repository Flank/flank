package flank.tool.analytics

import com.fasterxml.jackson.core.type.TypeReference
import kotlin.reflect.KClass

annotation class IgnoreInStatistics
annotation class AnonymizeInStatistics

internal val keysToRemove by lazy {
    classesForStatistics.map(findMembersWithAnnotation(IgnoreInStatistics::class)).flatten()
}

internal val keysToAnonymize by lazy {
    classesForStatistics.map(findMembersWithAnnotation(AnonymizeInStatistics::class)).flatten()
}

lateinit var classesForStatistics: List<KClass<*>>

private fun findMembersWithAnnotation(
    annotationType: KClass<*>
): KClass<*>.() -> List<String> = {
    members.filter { member ->
        member.annotations.any { annotation -> annotation.annotationClass == annotationType }
    }.map {
        it.name
    }
}

fun Map<String, Any>.removeNotNeededKeys() =
    filterNot { (key, _) ->
        key in keysToRemove
    }

fun Map<String, Any>.filterSensitiveValues() = mapValues { it.anonymousSensitiveValues() }

private fun Map.Entry<String, Any>.anonymousSensitiveValues() =
    if (keysToAnonymize.contains(key)) value.toAnonymous()
    else value

private fun Any.toAnonymous(): Any = when (this) {
    is Map<*, *> -> mapValues { ANONYMIZE_VALUE }
    is List<*> -> "Count: $size"
    else -> ANONYMIZE_VALUE
}

private const val ANONYMIZE_VALUE = "..."

fun Any.objectToMap(): Map<String, Any> = objectMapper.convertValue(this, object : TypeReference<Map<String, Any>>() {})
