package flank.tool.analytics.mixpanel.internal

import flank.tool.analytics.AnonymizeInStatistics
import flank.tool.analytics.IgnoreInStatistics
import kotlin.reflect.KClass

internal fun configureReport(
    projectName: String,
    blockUsageStatistics: Boolean,
    statisticClasses: Array<out KClass<*>>
) {
    Report.projectName = projectName
    Report.blockSendUsageStatistics = blockUsageStatistics
    Report.keysToRemove = statisticClasses getMembersWith AnonymizeInStatistics::class
    Report.keysToAnonymize = statisticClasses getMembersWith IgnoreInStatistics::class
}

private infix fun Array<out KClass<*>>.getMembersWith(
    annotationType: KClass<*>
): Set<String> =
    flatMap { type ->
        type.members.filter { member ->
            member.annotations.any { annotation ->
                annotation.annotationClass == annotationType
            }
        }
    }.map { member ->
        member.name
    }.toSet()
