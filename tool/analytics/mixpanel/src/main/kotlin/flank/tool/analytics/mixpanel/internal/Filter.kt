package flank.tool.analytics.mixpanel.internal

import flank.tool.analytics.AnonymizeInStatistics
import flank.tool.analytics.IgnoreInStatistics
import flank.tool.analytics.mixpanel.ObjectMap
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

// ============================Remove not needed ============================

internal fun ObjectMap.removeNotNeededKeys(): ObjectMap = filterNot { (key, _) -> key in keysToRemove }

private val keysToRemove by lazy {
    getClassesForStatisticsOrThrow()
        .map(findMembersWithAnnotation(IgnoreInStatistics::class))
        .flatten()
}

// ============================ Remove sensitive ============================

internal fun ObjectMap.removeSensitiveValues(): ObjectMap = mapValues { (key, value) ->
    when {
        key !in keysToAnonymize -> value
        value is Map<*, *> -> value.mapValues { ANONYMIZE_VALUE }
        value is List<*> -> "Count: $size"
        else -> ANONYMIZE_VALUE
    }
}

private val keysToAnonymize by lazy {
    getClassesForStatisticsOrThrow()
        .map(findMembersWithAnnotation(AnonymizeInStatistics::class))
        .flatten()
}

private const val ANONYMIZE_VALUE = "..."

// ============================ Common ============================

private fun getClassesForStatisticsOrThrow() =
    (Report.classesForStatistics ?: throw NullPointerException("Analytics client should be initialized first"))

private fun findMembersWithAnnotation(annotationType: KClass<*>) = fun KClass<*>.() = members
    .filter { member -> member.annotations.any { annotation -> annotation.annotationClass == annotationType } }
    .map(KCallable<*>::name)

