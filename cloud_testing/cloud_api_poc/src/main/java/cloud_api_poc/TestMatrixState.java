package cloud_api_poc;

public enum TestMatrixState {
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

  public String description;

  TestMatrixState(String description) {
    this.description = description;
  }

  public String value() {
    return this.toString();
  }

  public static boolean infastructureFailure(String testMatrixId) {
    // Must refresh to get accurate state
    String state = GcTestMatrix.refresh(testMatrixId).getState();
    return state != null && state.equals(ERROR.value());
  }

  public static boolean validatingOrPending(String testMatrixId) {
    // Must refresh to get accurate state
    String state = GcTestMatrix.refresh(testMatrixId).getState();
    return state == null || state.equals(VALIDATING.value()) || state.equals(PENDING.value());
  }
}
