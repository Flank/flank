package ftl.shard

import com.google.common.annotations.VisibleForTesting

// When a test does not have previous results to reference, fall back to this run time.
@VisibleForTesting
const val DEFAULT_TEST_TIME_SEC = 120.0
const val IGNORE_TEST_TIME = 0.0