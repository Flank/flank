package flank.junit.internal

import flank.junit.JUnit

internal fun List<JUnit.TestResult>.mergeDurations(
    forClasses: Set<String>
): List<JUnit.TestResult> =
    if (isEmpty() || forClasses.isEmpty()) this // Nothing to merge.
    else groupBy { method -> method.className in forClasses }.run { // separate test cases methods to merge.
        getOrDefault(false, emptyList()) + getOrDefault(true, emptyList()) // Sum test cases methods with classes.
            .groupBy { method -> method.className }.values // Group tests cases to merge by class name.
            .map { methods -> methods.sortedBy(JUnit.TestResult::startAt) } // Ensure correct order.
            .map { methods -> methods.first().copy(testName = "", endsAt = methods.last().endsAt) } // Merge into class.
    }
