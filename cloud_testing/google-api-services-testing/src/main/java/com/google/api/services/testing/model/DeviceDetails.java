package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public final class DeviceDetails extends GenericJson {
  @Key private ConnectionInfo connectionInfo;
  @Key private GceInstanceDetails gceInstanceDetails;

  public ConnectionInfo getConnectionInfo() {
    return this.connectionInfo;
  }

  public DeviceDetails setConnectionInfo(ConnectionInfo connectionInfo) {
    this.connectionInfo = connectionInfo;
    return this;
  }

  public GceInstanceDetails getGceInstanceDetails() {
    return this.gceInstanceDetails;
  }

  public DeviceDetails setGceInstanceDetails(GceInstanceDetails gceInstanceDetails) {
    this.gceInstanceDetails = gceInstanceDetails;
    return this;
  }

  public DeviceDetails set(String fieldName, Object value) {
    return (DeviceDetails) super.set(fieldName, value);
  }

  public DeviceDetails clone() {
    return (DeviceDetails) super.clone();
  }
}
