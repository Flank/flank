package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;


/**
 * Gets or Sets OrchestratorOption
 */
public enum OrchestratorOption {
  
  
  
  
  ORCHESTRATOR_OPTION_UNSPECIFIED("ORCHESTRATOR_OPTION_UNSPECIFIED"),
  
  
  USE_ORCHESTRATOR("USE_ORCHESTRATOR"),
  
  
  DO_NOT_USE_ORCHESTRATOR("DO_NOT_USE_ORCHESTRATOR");
  
  
  

  private String value;

  OrchestratorOption(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static OrchestratorOption fromValue(String text) {
    for (OrchestratorOption b : OrchestratorOption.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}



