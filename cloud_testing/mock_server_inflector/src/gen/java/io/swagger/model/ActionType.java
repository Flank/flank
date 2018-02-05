package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;


/**
 * Gets or Sets ActionType
 */
public enum ActionType {
  
  
  
  
  ACTION_TYPE_UNSPECIFIED("ACTION_TYPE_UNSPECIFIED"),
  
  
  SINGLE_CLICK("SINGLE_CLICK"),
  
  
  ENTER_TEXT("ENTER_TEXT");
  
  
  

  private String value;

  ActionType(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ActionType fromValue(String text) {
    for (ActionType b : ActionType.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}



