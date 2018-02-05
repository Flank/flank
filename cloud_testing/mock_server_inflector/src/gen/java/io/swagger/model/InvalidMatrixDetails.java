package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;


/**
 * Gets or Sets InvalidMatrixDetails
 */
public enum InvalidMatrixDetails {
  
  
  
  
  INVALID_MATRIX_DETAILS_UNSPECIFIED("INVALID_MATRIX_DETAILS_UNSPECIFIED"),
  
  
  DETAILS_UNAVAILABLE("DETAILS_UNAVAILABLE"),
  
  
  MALFORMED_APK("MALFORMED_APK"),
  
  
  MALFORMED_TEST_APK("MALFORMED_TEST_APK"),
  
  
  NO_MANIFEST("NO_MANIFEST"),
  
  
  NO_PACKAGE_NAME("NO_PACKAGE_NAME"),
  
  
  TEST_SAME_AS_APP("TEST_SAME_AS_APP"),
  
  
  NO_INSTRUMENTATION("NO_INSTRUMENTATION"),
  
  
  NO_SIGNATURE("NO_SIGNATURE"),
  
  
  INSTRUMENTATION_ORCHESTRATOR_INCOMPATIBLE("INSTRUMENTATION_ORCHESTRATOR_INCOMPATIBLE"),
  
  
  NO_TEST_RUNNER_CLASS("NO_TEST_RUNNER_CLASS"),
  
  
  NO_LAUNCHER_ACTIVITY("NO_LAUNCHER_ACTIVITY"),
  
  
  FORBIDDEN_PERMISSIONS("FORBIDDEN_PERMISSIONS"),
  
  
  INVALID_ROBO_DIRECTIVES("INVALID_ROBO_DIRECTIVES"),
  
  
  TEST_LOOP_INTENT_FILTER_NOT_FOUND("TEST_LOOP_INTENT_FILTER_NOT_FOUND"),
  
  
  SCENARIO_LABEL_NOT_DECLARED("SCENARIO_LABEL_NOT_DECLARED"),
  
  
  SCENARIO_LABEL_MALFORMED("SCENARIO_LABEL_MALFORMED"),
  
  
  SCENARIO_NOT_DECLARED("SCENARIO_NOT_DECLARED"),
  
  
  DEVICE_ADMIN_RECEIVER("DEVICE_ADMIN_RECEIVER"),
  
  
  TEST_ONLY_APK("TEST_ONLY_APK");
  
  
  

  private String value;

  InvalidMatrixDetails(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static InvalidMatrixDetails fromValue(String text) {
    for (InvalidMatrixDetails b : InvalidMatrixDetails.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}



