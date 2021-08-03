package flank.tool.analytics.mixpanel.internal

import flank.tool.analytics.AnonymizeInStatistics
import flank.tool.analytics.IgnoreInStatistics
import kotlin.reflect.KClass

internal fun Map<String, Any>.removeNotNeededKeys(): Map<String, Any> =
    filterNot { (key, _) -> key in keysToRemove }

internal fun Map<String, Any>.filterSensitiveValues(): Map<String, Any> =
    mapValues { it.anonymousSensitiveValues() }

private val keysToRemove by lazy {
    getClassesForStatisticsOrThrow().map(findMembersWithAnnotation(IgnoreInStatistics::class)).flatten()
}

private val keysToAnonymize by lazy {
    getClassesForStatisticsOrThrow().map(findMembersWithAnnotation(AnonymizeInStatistics::class)).flatten()
}

private fun getClassesForStatisticsOrThrow() =
    (AnalyticsReport.classesForStatistics ?: throw NullPointerException("Analytics client should be initialized first"))

private fun findMembersWithAnnotation(
    annotationType: KClass<*>
): KClass<*>.() -> List<String> = {
    members.filter { member ->
        member.annotations.any { annotation -> annotation.annotationClass == annotationType }
    }.map {
        it.name
    }
}

private fun Map.Entry<String, Any>.anonymousSensitiveValues() =
    if (keysToAnonymize.contains(key)) value.toAnonymous()
    else value

private fun Any.toAnonymous(): Any = when (this) {
    is Map<*, *> -> mapValues { ANONYMIZE_VALUE }
    is List<*> -> "Count: $size"
    else -> ANONYMIZE_VALUE
}

private const val ANONYMIZE_VALUE = "..."
