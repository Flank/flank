package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;


/**
 * Gets or Sets State
 */
public enum State {
  
  
  
  
  TEST_STATE_UNSPECIFIED("TEST_STATE_UNSPECIFIED"),
  
  
  VALIDATING("VALIDATING"),
  
  
  PENDING("PENDING"),
  
  
  RUNNING("RUNNING"),
  
  
  FINISHED("FINISHED"),
  
  
  ERROR("ERROR"),
  
  
  UNSUPPORTED_ENVIRONMENT("UNSUPPORTED_ENVIRONMENT"),
  
  
  INCOMPATIBLE_ENVIRONMENT("INCOMPATIBLE_ENVIRONMENT"),
  
  
  INCOMPATIBLE_ARCHITECTURE("INCOMPATIBLE_ARCHITECTURE"),
  
  
  CANCELLED("CANCELLED"),
  
  
  INVALID("INVALID");
  
  
  

  private String value;

  State(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static State fromValue(String text) {
    for (State b : State.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}



