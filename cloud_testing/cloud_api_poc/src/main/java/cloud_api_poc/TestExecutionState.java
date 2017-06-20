package cloud_api_poc;

public abstract class TestExecutionState {
  // https://github.com/bootstraponline/studio-google-cloud-testing/blob/203ed2890c27a8078cd1b8f7ae12cf77527f426b/firebase-testing/src/com/google/gct/testing/CloudResultsLoader.java#L237

  // fatalError("Incompatible device/OS combination");
  public static String UNSUPPORTED_ENVIRONMENT = "UNSUPPORTED_ENVIRONMENT";
  public static String VALIDATING = "VALIDATING";

  // fatalError("Application does not support the specified OS version");
  public static String INCOMPATIBLE_ENVIRONMENT = "INCOMPATIBLE_ENVIRONMENT";

  // fatalError("The provided APK is invalid");
  public static String INVALID = "INVALID";

  // fatalError("Application does not support the specified device architecture");
  public static String INCOMPATIBLE_ARCHITECTURE = "INCOMPATIBLE_ARCHITECTURE";
  public static String PENDING = "PENDING";

  // INFRASTRUCTURE_FAILURE.  testExecution.getTestDetails().getErrorMessage());
  public static String ERROR = "ERROR";
  // testExecution.getTestDetails().getProgressMessages();
  public static String RUNNING = "RUNNING";
  public static String FINISHED = "FINISHED";

  public static boolean isValid(String state) {
    if (state == null) return false;

    boolean valid;

    if (state.equals(UNSUPPORTED_ENVIRONMENT)) {
      valid = false;
    } else if (state.equals(VALIDATING)) {
      valid = true;
    } else if (state.equals(INCOMPATIBLE_ENVIRONMENT)) {
      valid = false;
    } else if (state.equals(INVALID)) {
      valid = false;
    } else if (state.equals(INCOMPATIBLE_ARCHITECTURE)) {
      valid = false;
    } else if (state.equals(PENDING)) {
      valid = true;
    } else if (state.equals(ERROR)) {
      valid = false;
    } else if (state.equals(RUNNING)) {
      valid = true;
    } else if (state.equals(FINISHED)) {
      valid = true;
    } else {
      throw new IllegalStateException("Unknown state: " + state);
    }

    return valid;
  }
}
