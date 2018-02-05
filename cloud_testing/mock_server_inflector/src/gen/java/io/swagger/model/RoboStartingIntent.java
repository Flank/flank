package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Message for specifying the start activities to crawl
 **/@ApiModel(description = "Message for specifying the start activities to crawl")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class RoboStartingIntent   {
  
  @JsonProperty("startActivity")
  private Object startActivity = null;
  
  @JsonProperty("launcherActivity")
  private Object launcherActivity = null;
  
  /**
   **/
  public RoboStartingIntent startActivity(Object startActivity) {
    this.startActivity = startActivity;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("startActivity")
  public Object getStartActivity() {
    return startActivity;
  }
  public void setStartActivity(Object startActivity) {
    this.startActivity = startActivity;
  }

  /**
   **/
  public RoboStartingIntent launcherActivity(Object launcherActivity) {
    this.launcherActivity = launcherActivity;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("launcherActivity")
  public Object getLauncherActivity() {
    return launcherActivity;
  }
  public void setLauncherActivity(Object launcherActivity) {
    this.launcherActivity = launcherActivity;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoboStartingIntent roboStartingIntent = (RoboStartingIntent) o;
    return Objects.equals(startActivity, roboStartingIntent.startActivity) &&
        Objects.equals(launcherActivity, roboStartingIntent.launcherActivity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startActivity, launcherActivity);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoboStartingIntent {\n");
    
    sb.append("    startActivity: ").append(toIndentedString(startActivity)).append("\n");
    sb.append("    launcherActivity: ").append(toIndentedString(launcherActivity)).append("\n");
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



