package ftl.analytics

import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import kotlin.reflect.KClass

annotation class IgnoreInStatistics
annotation class AnonymizeInStatistics

internal val keysToRemove by lazy {
    classesForStatistics.map { it.ignoredMembersForStatistics() }.flatten()
}

private fun KClass<*>.ignoredMembersForStatistics() = findMembersWithAnnotation(IgnoreInStatistics::class)

internal val keysToAnonymize by lazy {
    classesForStatistics.map { it.anonymousMembersForStatistics() }.flatten()
}

private fun KClass<*>.anonymousMembersForStatistics() = findMembersWithAnnotation(AnonymizeInStatistics::class)

private val classesForStatistics = listOf(IArgs::class, AndroidArgs::class, IosArgs::class)

private const val ANONYMIZE_VALUE = "..."

private fun KClass<*>.findMembersWithAnnotation(annotationType: KClass<*>) = members.filter { member ->
    member.annotations.any { annotation -> annotation.annotationClass == annotationType }
}.map {
    it.name
}

internal fun Map<String, Any>.removeNotNeededKeys(defaultArgs: Map<String, Any>) =
    filterNot { (key, value) ->
        value == defaultArgs[key] || key in keysToRemove
    }

internal fun Map<String, Any>.filterSensitiveValues() = mapValues { it.anonymousSensitiveValues() }

private fun Map.Entry<String, Any>.anonymousSensitiveValues() =
    if (keysToAnonymize.contains(key)) value.toAnonymous()
    else value

private fun Any.toAnonymous(): Any = when (this) {
    is Map<*, *> -> mapValues { ANONYMIZE_VALUE }
    is List<*> -> "Count: $size"
    else -> ANONYMIZE_VALUE
}
