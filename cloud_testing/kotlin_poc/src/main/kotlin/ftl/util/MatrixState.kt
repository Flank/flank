package ftl.util

object MatrixState {
    // https://github.com/bootstraponline/studio-google-cloud-testing/blob/203ed2890c27a8078cd1b8f7ae12cf77527f426b/firebase-testing/src/com/google/gct/testing/CloudResultsLoader.java#L237

    // fatalError("Incompatible device/OS combination");
    var UNSUPPORTED_ENVIRONMENT = "UNSUPPORTED_ENVIRONMENT"
    var VALIDATING = "VALIDATING"

    // fatalError("Application does not support the specified OS version");
    var INCOMPATIBLE_ENVIRONMENT = "INCOMPATIBLE_ENVIRONMENT"

    // fatalError("The provided APK is invalid");
    var INVALID = "INVALID"

    // fatalError("Application does not support the specified device architecture");
    var INCOMPATIBLE_ARCHITECTURE = "INCOMPATIBLE_ARCHITECTURE"
    var PENDING = "PENDING"

    // INFRASTRUCTURE_FAILURE.  testExecution.getTestDetails().getErrorMessage());
    var ERROR = "ERROR"
    // testExecution.getTestDetails().getProgressMessages();
    var RUNNING = "RUNNING"
    var FINISHED = "FINISHED"

    fun inProgress(state: String): Boolean {
        return when (state) {
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
}
