object TestExecutionState {
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

    fun isValid(state: String?): Boolean {
        if (state == null) return false

        val valid: Boolean

        if (state == UNSUPPORTED_ENVIRONMENT) {
            valid = false
        } else if (state == VALIDATING) {
            valid = true
        } else if (state == INCOMPATIBLE_ENVIRONMENT) {
            valid = false
        } else if (state == INVALID) {
            valid = false
        } else if (state == INCOMPATIBLE_ARCHITECTURE) {
            valid = false
        } else if (state == PENDING) {
            valid = true
        } else if (state == ERROR) {
            valid = false
        } else if (state == RUNNING) {
            valid = true
        } else if (state == FINISHED) {
            valid = true
        } else {
            throw IllegalStateException("Unknown state: " + state)
        }

        return valid
    }
}
