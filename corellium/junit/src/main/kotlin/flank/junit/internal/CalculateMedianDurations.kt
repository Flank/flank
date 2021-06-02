package flank.junit.internal

import flank.junit.JUnit

/**
 * Group test case results by full name and associate with calculated median of durations in group.
 */
internal fun List<JUnit.TestResult>.calculateMedianDurations(): Map<String, Long> = this
    .map { result -> result.fullName to result.duration }
    .groupBy(Pair<String, Long>::first, Pair<String, Long>::second)
    .mapValues { (_, durations) -> durations.median() }

private val JUnit.TestResult.fullName get() = "$className#$testName"

private val JUnit.TestResult.duration get() = (endsAt - startAt)

private fun List<Long>.median(): Long = when {
    isEmpty() -> throw IllegalArgumentException("Cannot calculate median of empty list")
    size % 2 == 0 -> (size / 2).let { get(it - 1) + get(it) } / 2
    else -> get(size / 2)
}
