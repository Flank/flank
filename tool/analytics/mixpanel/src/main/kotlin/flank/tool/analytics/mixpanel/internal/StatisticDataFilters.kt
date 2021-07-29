package flank.tool.analytics.mixpanel.internal

import flank.tool.analytics.AnonymizeInStatistics
import flank.tool.analytics.IgnoreInStatistics
import kotlin.reflect.KClass

internal val keysToRemove by lazy {
    getClassesForStatisticsOrThrow().map(findMembersWithAnnotation(IgnoreInStatistics::class)).flatten()
}

internal val keysToAnonymize by lazy {
    getClassesForStatisticsOrThrow().map(findMembersWithAnnotation(AnonymizeInStatistics::class)).flatten()
}

private fun getClassesForStatisticsOrThrow() =
    (classesForStatistics ?: throw NullPointerException("Analytics client should be initialized first"))

internal var classesForStatistics: List<KClass<*>>? = null

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
