package ftl.shard

import com.google.common.annotations.VisibleForTesting
import ftl.util.FlankTestMethod

// When a test does not have previous results to reference, fall back to this run time.
@VisibleForTesting
const val DEFAULT_TEST_TIME_SEC = 120.0

@VisibleForTesting
const val IGNORE_TEST_TIME = 0.0

fun getTestMethodTime(flankTestMethod: FlankTestMethod, previousMethodDurations: Map<String, Double>): Double {
    return if (flankTestMethod.ignored) IGNORE_TEST_TIME else previousMethodDurations.getOrDefault(
        flankTestMethod.testName,
        DEFAULT_TEST_TIME_SEC
    )
}
