package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Represents a whole calendar date, e.g. date of birth. The time of day and time zone are either specified elsewhere or are not significant. The date is relative to the Proleptic Gregorian Calendar. The day may be 0 to represent a year and month where the day is not significant, e.g. credit card expiration date. The year may be 0 to represent a month and day independent of year, e.g. anniversary date. Related types are google.type.TimeOfDay and &#x60;google.protobuf.Timestamp&#x60;.
 **/@ApiModel(description = "Represents a whole calendar date, e.g. date of birth. The time of day and time zone are either specified elsewhere or are not significant. The date is relative to the Proleptic Gregorian Calendar. The day may be 0 to represent a year and month where the day is not significant, e.g. credit card expiration date. The year may be 0 to represent a month and day independent of year, e.g. anniversary date. Related types are google.type.TimeOfDay and `google.protobuf.Timestamp`.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class Date20   {
  
  @JsonProperty("year")
  private Integer year = null;
  
  @JsonProperty("day")
  private Integer day = null;
  
  @JsonProperty("month")
  private Integer month = null;
  
  /**
   * Year of date. Must be from 1 to 9999, or 0 if specifying a date without a year.
   **/
  public Date20 year(Integer year) {
    this.year = year;
    return this;
  }

  @ApiModelProperty(value = "Year of date. Must be from 1 to 9999, or 0 if specifying a date without a year.")
  @JsonProperty("year")
  public Integer getYear() {
    return year;
  }
  public void setYear(Integer year) {
    this.year = year;
  }

  /**
   * Day of month. Must be from 1 to 31 and valid for the year and month, or 0 if specifying a year/month where the day is not significant.
   **/
  public Date20 day(Integer day) {
    this.day = day;
    return this;
  }

  @ApiModelProperty(value = "Day of month. Must be from 1 to 31 and valid for the year and month, or 0 if specifying a year/month where the day is not significant.")
  @JsonProperty("day")
  public Integer getDay() {
    return day;
  }
  public void setDay(Integer day) {
    this.day = day;
  }

  /**
   * Month of year. Must be from 1 to 12.
   **/
  public Date20 month(Integer month) {
    this.month = month;
    return this;
  }

  @ApiModelProperty(value = "Month of year. Must be from 1 to 12.")
  @JsonProperty("month")
  public Integer getMonth() {
    return month;
  }
  public void setMonth(Integer month) {
    this.month = month;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Date20 date20 = (Date20) o;
    return Objects.equals(year, date20.year) &&
        Objects.equals(day, date20.day) &&
        Objects.equals(month, date20.month);
  }

  @Override
  public int hashCode() {
    return Objects.hash(year, day, month);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Date20 {\n");
    
    sb.append("    year: ").append(toIndentedString(year)).append("\n");
    sb.append("    day: ").append(toIndentedString(day)).append("\n");
    sb.append("    month: ").append(toIndentedString(month)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}



