package ftl.util

object MatrixState {
    // https://github.com/bootstraponline/studio-google-cloud-testing/blob/203ed2890c27a8078cd1b8f7ae12cf77527f426b/firebase-testing/src/com/google/gct/testing/CloudResultsLoader.java#L237
    // see testing_v1.json TestMatrix state enum

    private const val TEST_STATE_UNSPECIFIED = "TEST_STATE_UNSPECIFIED"
    private const val VALIDATING = "VALIDATING"
    private const val PENDING = "PENDING"
    private const val RUNNING = "RUNNING"
    const val FINISHED = "FINISHED"
    private const val ERROR = "ERROR"
    private const val UNSUPPORTED_ENVIRONMENT = "UNSUPPORTED_ENVIRONMENT" // Incompatible device/OS combination
    private const val INCOMPATIBLE_ENVIRONMENT =
        "INCOMPATIBLE_ENVIRONMENT" // Application does not support the specified OS version
    private const val INCOMPATIBLE_ARCHITECTURE =
        "INCOMPATIBLE_ARCHITECTURE" // Application does not support the specified device architecture
    private const val CANCELLED = "CANCELLED"
    private const val INVALID = "INVALID" // The provided APK is invalid

    fun inProgress(state: String): Boolean {
        return when (state) {
            CANCELLED -> false
            TEST_STATE_UNSPECIFIED -> false
            UNSUPPORTED_ENVIRONMENT -> false
            VALIDATING -> true
            INCOMPATIBLE_ENVIRONMENT -> false
            INVALID -> false
            INCOMPATIBLE_ARCHITECTURE -> false
            PENDING -> true
            ERROR -> false
            RUNNING -> true
            FINISHED -> false
            else -> {
                throw RuntimeException("Unknown state: $state")
            }
        }
    }

    fun completed(state: String): Boolean {
        return !inProgress(state)
    }
}
