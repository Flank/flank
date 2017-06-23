package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public final class Device extends GenericJson {
  @Key private AndroidDevice androidDevice;
  @Key private String creationTime;
  @Key private DeviceDetails deviceDetails;
  @Key private String id;
  @Key private String state;
  @Key private DeviceStateDetails stateDetails;

  public AndroidDevice getAndroidDevice() {
    return this.androidDevice;
  }

  public Device setAndroidDevice(AndroidDevice androidDevice) {
    this.androidDevice = androidDevice;
    return this;
  }

  public String getCreationTime() {
    return this.creationTime;
  }

  public Device setCreationTime(String creationTime) {
    this.creationTime = creationTime;
    return this;
  }

  public DeviceDetails getDeviceDetails() {
    return this.deviceDetails;
  }

  public Device setDeviceDetails(DeviceDetails deviceDetails) {
    this.deviceDetails = deviceDetails;
    return this;
  }

  public String getId() {
    return this.id;
  }

  public Device setId(String id) {
    this.id = id;
    return this;
  }

  public String getState() {
    return this.state;
  }

  public Device setState(String state) {
    this.state = state;
    return this;
  }

  public DeviceStateDetails getStateDetails() {
    return this.stateDetails;
  }

  public Device setStateDetails(DeviceStateDetails stateDetails) {
    this.stateDetails = stateDetails;
    return this;
  }

  public Device set(String fieldName, Object value) {
    return (Device) super.set(fieldName, value);
  }

  public Device clone() {
    return (Device) super.clone();
  }
}
