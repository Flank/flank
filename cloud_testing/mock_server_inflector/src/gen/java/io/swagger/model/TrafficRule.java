package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;

/**
 * Network emulation parameters
 **/@ApiModel(description = "Network emulation parameters")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class TrafficRule   {
  
  @JsonProperty("packetLossRatio")
  private BigDecimal packetLossRatio = null;
  
  @JsonProperty("burst")
  private BigDecimal burst = null;
  
  @JsonProperty("bandwidth")
  private BigDecimal bandwidth = null;
  
  @JsonProperty("packetDuplicationRatio")
  private BigDecimal packetDuplicationRatio = null;
  
  @JsonProperty("delay")
  private String delay = null;
  
  /**
   * Packet loss ratio (0.0 - 1.0)
   **/
  public TrafficRule packetLossRatio(BigDecimal packetLossRatio) {
    this.packetLossRatio = packetLossRatio;
    return this;
  }

  @ApiModelProperty(value = "Packet loss ratio (0.0 - 1.0)")
  @JsonProperty("packetLossRatio")
  public BigDecimal getPacketLossRatio() {
    return packetLossRatio;
  }
  public void setPacketLossRatio(BigDecimal packetLossRatio) {
    this.packetLossRatio = packetLossRatio;
  }

  /**
   * Burst size in kbits
   **/
  public TrafficRule burst(BigDecimal burst) {
    this.burst = burst;
    return this;
  }

  @ApiModelProperty(value = "Burst size in kbits")
  @JsonProperty("burst")
  public BigDecimal getBurst() {
    return burst;
  }
  public void setBurst(BigDecimal burst) {
    this.burst = burst;
  }

  /**
   * Bandwidth in kbits/second
   **/
  public TrafficRule bandwidth(BigDecimal bandwidth) {
    this.bandwidth = bandwidth;
    return this;
  }

  @ApiModelProperty(value = "Bandwidth in kbits/second")
  @JsonProperty("bandwidth")
  public BigDecimal getBandwidth() {
    return bandwidth;
  }
  public void setBandwidth(BigDecimal bandwidth) {
    this.bandwidth = bandwidth;
  }

  /**
   * Packet duplication ratio (0.0 - 1.0)
   **/
  public TrafficRule packetDuplicationRatio(BigDecimal packetDuplicationRatio) {
    this.packetDuplicationRatio = packetDuplicationRatio;
    return this;
  }

  @ApiModelProperty(value = "Packet duplication ratio (0.0 - 1.0)")
  @JsonProperty("packetDuplicationRatio")
  public BigDecimal getPacketDuplicationRatio() {
    return packetDuplicationRatio;
  }
  public void setPacketDuplicationRatio(BigDecimal packetDuplicationRatio) {
    this.packetDuplicationRatio = packetDuplicationRatio;
  }

  /**
   * Packet delay, must be >= 0
   **/
  public TrafficRule delay(String delay) {
    this.delay = delay;
    return this;
  }

  @ApiModelProperty(value = "Packet delay, must be >= 0")
  @JsonProperty("delay")
  public String getDelay() {
    return delay;
  }
  public void setDelay(String delay) {
    this.delay = delay;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TrafficRule trafficRule = (TrafficRule) o;
    return Objects.equals(packetLossRatio, trafficRule.packetLossRatio) &&
        Objects.equals(burst, trafficRule.burst) &&
        Objects.equals(bandwidth, trafficRule.bandwidth) &&
        Objects.equals(packetDuplicationRatio, trafficRule.packetDuplicationRatio) &&
        Objects.equals(delay, trafficRule.delay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(packetLossRatio, burst, bandwidth, packetDuplicationRatio, delay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TrafficRule {\n");
    
    sb.append("    packetLossRatio: ").append(toIndentedString(packetLossRatio)).append("\n");
    sb.append("    burst: ").append(toIndentedString(burst)).append("\n");
    sb.append("    bandwidth: ").append(toIndentedString(bandwidth)).append("\n");
    sb.append("    packetDuplicationRatio: ").append(toIndentedString(packetDuplicationRatio)).append("\n");
    sb.append("    delay: ").append(toIndentedString(delay)).append("\n");
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



