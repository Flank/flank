package flank.junit.internal

import flank.junit.JUnit

internal fun List<JUnit.TestResult>.mergeDurations(
    forClasses: Set<String>
): List<JUnit.TestResult> =
    if (forClasses.isEmpty()) this
    else groupBy { testResult -> testResult.className in forClasses }.run {
        getOrDefault(false, emptyList()) + getOrDefault(true, emptyList())
            .groupBy { testResult -> testResult.className }.values
            .map { group ->
                group.sortedBy { it.startAt }
                    .reduce { acc, testResult -> acc.copy(endsAt = testResult.endsAt) }
                    .run { copy(testName = "") }
            }
    }
