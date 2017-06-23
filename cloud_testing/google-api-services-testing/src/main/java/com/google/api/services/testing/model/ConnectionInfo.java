package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public final class ConnectionInfo extends GenericJson {
  @Key private Integer adbPort;
  @Key private String ipAddress;
  @Key private Integer sshPort;
  @Key private String vncPassword;
  @Key private Integer vncPort;

  public Integer getAdbPort() {
    return this.adbPort;
  }

  public ConnectionInfo setAdbPort(Integer adbPort) {
    this.adbPort = adbPort;
    return this;
  }

  public String getIpAddress() {
    return this.ipAddress;
  }

  public ConnectionInfo setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
    return this;
  }

  public Integer getSshPort() {
    return this.sshPort;
  }

  public ConnectionInfo setSshPort(Integer sshPort) {
    this.sshPort = sshPort;
    return this;
  }

  public String getVncPassword() {
    return this.vncPassword;
  }

  public ConnectionInfo setVncPassword(String vncPassword) {
    this.vncPassword = vncPassword;
    return this;
  }

  public Integer getVncPort() {
    return this.vncPort;
  }

  public ConnectionInfo setVncPort(Integer vncPort) {
    this.vncPort = vncPort;
    return this;
  }

  public ConnectionInfo set(String fieldName, Object value) {
    return (ConnectionInfo) super.set(fieldName, value);
  }

  public ConnectionInfo clone() {
    return (ConnectionInfo) super.clone();
  }
}
