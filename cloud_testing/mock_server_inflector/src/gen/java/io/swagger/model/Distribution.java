package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;

/**
 * Data about the relative number of devices running a given configuration of the Android platform.
 **/@ApiModel(description = "Data about the relative number of devices running a given configuration of the Android platform.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class Distribution   {
  
  @JsonProperty("marketShare")
  private BigDecimal marketShare = null;
  
  @JsonProperty("measurementTime")
  private String measurementTime = null;
  
  /**
   * The estimated fraction (0-1) of the total market with this configuration. @OutputOnly
   **/
  public Distribution marketShare(BigDecimal marketShare) {
    this.marketShare = marketShare;
    return this;
  }

  @ApiModelProperty(value = "The estimated fraction (0-1) of the total market with this configuration. @OutputOnly")
  @JsonProperty("marketShare")
  public BigDecimal getMarketShare() {
    return marketShare;
  }
  public void setMarketShare(BigDecimal marketShare) {
    this.marketShare = marketShare;
  }

  /**
   * The time this distribution was measured. @OutputOnly
   **/
  public Distribution measurementTime(String measurementTime) {
    this.measurementTime = measurementTime;
    return this;
  }

  @ApiModelProperty(value = "The time this distribution was measured. @OutputOnly")
  @JsonProperty("measurementTime")
  public String getMeasurementTime() {
    return measurementTime;
  }
  public void setMeasurementTime(String measurementTime) {
    this.measurementTime = measurementTime;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Distribution distribution = (Distribution) o;
    return Objects.equals(marketShare, distribution.marketShare) &&
        Objects.equals(measurementTime, distribution.measurementTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(marketShare, measurementTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Distribution {\n");
    
    sb.append("    marketShare: ").append(toIndentedString(marketShare)).append("\n");
    sb.append("    measurementTime: ").append(toIndentedString(measurementTime)).append("\n");
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



