package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * A description of how to set up the device prior to running the test
 **/@ApiModel(description = "A description of how to set up the device prior to running the test")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class TestSetup   {
  
  @JsonProperty("networkProfile")
  private String networkProfile = null;
  
  @JsonProperty("environmentVariables")
  private List<Object> environmentVariables = null;
  
  @JsonProperty("account")
  private Object account = null;
  
  @JsonProperty("directoriesToPull")
  private List<String> directoriesToPull = null;
  
  @JsonProperty("filesToPush")
  private List<Object> filesToPush = null;
  
  /**
   * The network traffic profile used for running the test. Optional
   **/
  public TestSetup networkProfile(String networkProfile) {
    this.networkProfile = networkProfile;
    return this;
  }

  @ApiModelProperty(value = "The network traffic profile used for running the test. Optional")
  @JsonProperty("networkProfile")
  public String getNetworkProfile() {
    return networkProfile;
  }
  public void setNetworkProfile(String networkProfile) {
    this.networkProfile = networkProfile;
  }

  /**
   * Environment variables to set for the test (only applicable for instrumentation tests).
   **/
  public TestSetup environmentVariables(List<Object> environmentVariables) {
    this.environmentVariables = environmentVariables;
    return this;
  }

  @ApiModelProperty(value = "Environment variables to set for the test (only applicable for instrumentation tests).")
  @JsonProperty("environmentVariables")
  public List<Object> getEnvironmentVariables() {
    return environmentVariables;
  }
  public void setEnvironmentVariables(List<Object> environmentVariables) {
    this.environmentVariables = environmentVariables;
  }

  /**
   * The device will be logged in on this account for the duration of the test. Optional
   **/
  public TestSetup account(Object account) {
    this.account = account;
    return this;
  }

  @ApiModelProperty(value = "The device will be logged in on this account for the duration of the test. Optional")
  @JsonProperty("account")
  public Object getAccount() {
    return account;
  }
  public void setAccount(Object account) {
    this.account = account;
  }

  /**
   * List of directories on the device to upload to GCS at the end of the test; they must be absolute paths under /sdcard or /data/local/tmp. Path names are restricted to characters a-z A-Z 0-9 _ - . + and /  Note: The paths /sdcard and /data will be made available and treated as implicit path substitutions. E.g. if /sdcard on a particular device does not map to external storage, the system will replace it with the external storage path prefix for that device.  Optional
   **/
  public TestSetup directoriesToPull(List<String> directoriesToPull) {
    this.directoriesToPull = directoriesToPull;
    return this;
  }

  @ApiModelProperty(value = "List of directories on the device to upload to GCS at the end of the test; they must be absolute paths under /sdcard or /data/local/tmp. Path names are restricted to characters a-z A-Z 0-9 _ - . + and /  Note: The paths /sdcard and /data will be made available and treated as implicit path substitutions. E.g. if /sdcard on a particular device does not map to external storage, the system will replace it with the external storage path prefix for that device.  Optional")
  @JsonProperty("directoriesToPull")
  public List<String> getDirectoriesToPull() {
    return directoriesToPull;
  }
  public void setDirectoriesToPull(List<String> directoriesToPull) {
    this.directoriesToPull = directoriesToPull;
  }

  /**
   * List of files to push to the device before starting the test.  Optional
   **/
  public TestSetup filesToPush(List<Object> filesToPush) {
    this.filesToPush = filesToPush;
    return this;
  }

  @ApiModelProperty(value = "List of files to push to the device before starting the test.  Optional")
  @JsonProperty("filesToPush")
  public List<Object> getFilesToPush() {
    return filesToPush;
  }
  public void setFilesToPush(List<Object> filesToPush) {
    this.filesToPush = filesToPush;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TestSetup testSetup = (TestSetup) o;
    return Objects.equals(networkProfile, testSetup.networkProfile) &&
        Objects.equals(environmentVariables, testSetup.environmentVariables) &&
        Objects.equals(account, testSetup.account) &&
        Objects.equals(directoriesToPull, testSetup.directoriesToPull) &&
        Objects.equals(filesToPush, testSetup.filesToPush);
  }

  @Override
  public int hashCode() {
    return Objects.hash(networkProfile, environmentVariables, account, directoriesToPull, filesToPush);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TestSetup {\n");
    
    sb.append("    networkProfile: ").append(toIndentedString(networkProfile)).append("\n");
    sb.append("    environmentVariables: ").append(toIndentedString(environmentVariables)).append("\n");
    sb.append("    account: ").append(toIndentedString(account)).append("\n");
    sb.append("    directoriesToPull: ").append(toIndentedString(directoriesToPull)).append("\n");
    sb.append("    filesToPush: ").append(toIndentedString(filesToPush)).append("\n");
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



