package ftl.shard

import ftl.args.IArgs
import ftl.util.FlankTestMethod

fun getTestMethodTime(
    flankTestMethod: FlankTestMethod,
    previousMethodDurations: Map<String, Double>,
    defaultTestTime: Double,
    defaultClassTestTime: Double
) = if (flankTestMethod.ignored) IGNORE_TEST_TIME else previousMethodDurations.getOrElse(flankTestMethod.testName) {
    if (flankTestMethod.isParameterizedClass) defaultClassTestTime
    else defaultTestTime
}

fun IArgs.fallbackTestTime(
    previousMethodDurations: Map<String, Double>
) = if (useAverageTestTimeForNewTests) previousMethodDurations.averageTestTime(defaultTestTime) else defaultTestTime

fun IArgs.fallbackClassTestTime(
    previousMethodDurations: Map<String, Double>
) = if (useAverageTestTimeForNewTests) previousMethodDurations.averageTestTime(defaultClassTestTime) else defaultClassTestTime

private fun Map<String, Double>.averageTestTime(defaultTestTime: Double) = values
    .filter { it > IGNORE_TEST_TIME }
    .average()
    .takeIf { !it.isNaN() }
    ?: defaultTestTime

const val IGNORE_TEST_TIME = 0.0

const val DEFAULT_TEST_TIME_SEC = 120.0

const val DEFAULT_CLASS_TEST_TIME_SEC = DEFAULT_TEST_TIME_SEC * 2
