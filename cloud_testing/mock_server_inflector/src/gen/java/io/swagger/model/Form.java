package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;


/**
 * Gets or Sets Form
 */
public enum Form {
  
  
  
  
  DEVICE_FORM_UNSPECIFIED("DEVICE_FORM_UNSPECIFIED"),
  
  
  VIRTUAL("VIRTUAL"),
  
  
  PHYSICAL("PHYSICAL");
  
  
  

  private String value;

  Form(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static Form fromValue(String text) {
    for (Form b : Form.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}



