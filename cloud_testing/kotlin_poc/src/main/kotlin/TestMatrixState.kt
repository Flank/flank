enum class TestMatrixState private constructor(var description: String) {
    VALIDATING("The execution or matrix is being validated."),
    PENDING("The execution or matrix is waiting for resources to become available."),
    RUNNING("The execution is currently being processed.\n\nCan only be set on an execution."),
    FINISHED(
            "The execution or matrix has terminated normally.\n\nOn a matrix this means that the matrix level processing completed normally,\nbut individual executions may be in an ERROR state."),
    ERROR("The execution or matrix has stopped because it encountered an\ninfrastructure failure."),
    UNSUPPORTED_ENVIRONMENT(
            "The execution was not run because it corresponds to a unsupported\nenvironment.\n\nCan only be set on an execution."),
    INCOMPATIBLE_ENVIRONMENT(
            "The execution was not run because the provided inputs are incompatible with\nthe requested environment.\n\nExample: requested AndroidVersion is lower than APK's minSdkVersion\n\nCan only be set on an execution."),
    INCOMPATIBLE_ARCHITECTURE(
            "The execution was not run because the provided inputs are incompatible with\nthe requested architecture.\n\nExample: requested device does not support running the native code in\nthe supplied APK\n\nCan only be set on an execution."),
    CANCELLED("The user cancelled the execution.\n\nCan only be set on an execution."),
    INVALID(
            "The execution or matrix was not run because the provided inputs are not\nvalid.\n\nExamples: input file is not of the expected type, is malformed\\/corrupt, or\nwas flagged as malware");

    fun value(): String {
        return this.toString()
    }

    companion object {

        fun infastructureFailure(testMatrixId: String): Boolean {
            // Must refresh to get accurate state
            val state = GcTestMatrix.refresh(testMatrixId).state
            return state != null && state == ERROR.value()
        }

        fun validatingOrPending(testMatrixId: String): Boolean {
            // Must refresh to get accurate state
            val state = GcTestMatrix.refresh(testMatrixId).state
            return state == null || state == VALIDATING.value() || state == PENDING.value()
        }
    }
}
