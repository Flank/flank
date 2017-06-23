package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public final class Date extends GenericJson {
  @Key private Integer day;
  @Key private Integer month;
  @Key private Integer year;

  public Integer getDay() {
    return this.day;
  }

  public Date setDay(Integer day) {
    this.day = day;
    return this;
  }

  public Integer getMonth() {
    return this.month;
  }

  public Date setMonth(Integer month) {
    this.month = month;
    return this;
  }

  public Integer getYear() {
    return this.year;
  }

  public Date setYear(Integer year) {
    this.year = year;
    return this;
  }

  public Date set(String fieldName, Object value) {
    return (Date) super.set(fieldName, value);
  }

  public Date clone() {
    return (Date) super.clone();
  }
}
