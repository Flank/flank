package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * Information about the client which invoked the test.
 **/@ApiModel(description = "Information about the client which invoked the test.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class ClientInfo   {
  
  @JsonProperty("clientInfoDetails")
  private List<Object> clientInfoDetails = null;
  
  @JsonProperty("name")
  private String name = null;
  
  /**
   * The list of detailed information about client.
   **/
  public ClientInfo clientInfoDetails(List<Object> clientInfoDetails) {
    this.clientInfoDetails = clientInfoDetails;
    return this;
  }

  @ApiModelProperty(value = "The list of detailed information about client.")
  @JsonProperty("clientInfoDetails")
  public List<Object> getClientInfoDetails() {
    return clientInfoDetails;
  }
  public void setClientInfoDetails(List<Object> clientInfoDetails) {
    this.clientInfoDetails = clientInfoDetails;
  }

  /**
   * Client name, such as gcloud. Required
   **/
  public ClientInfo name(String name) {
    this.name = name;
    return this;
  }

  @ApiModelProperty(value = "Client name, such as gcloud. Required")
  @JsonProperty("name")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClientInfo clientInfo = (ClientInfo) o;
    return Objects.equals(clientInfoDetails, clientInfo.clientInfoDetails) &&
        Objects.equals(name, clientInfo.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clientInfoDetails, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClientInfo {\n");
    
    sb.append("    clientInfoDetails: ").append(toIndentedString(clientInfoDetails)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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



