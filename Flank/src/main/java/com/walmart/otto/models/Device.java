package com.walmart.otto.models;

import java.util.ArrayList;
import java.util.List;

public class Device {

  private final String id;
  private final String locale;
  private final String orientation;
  private final String osVersion;

  public String getId() {
    return id;
  }

  public String getLocale() {
    return locale;
  }

  public String getOrientation() {
    return orientation;
  }

  public String getOsVersion() {
    return osVersion;
  }

  @Override
  public String toString() {
    return "model="
        + id
        + ",locale="
        + locale
        + ",orientation="
        + orientation
        + ",version="
        + osVersion;
  }

  private Device(String id, String locale, String orientation, String osVersion) {
    this.id = id;
    this.locale = locale;
    this.orientation = orientation;
    this.osVersion = osVersion;
  }

  public static class Builder {

    private String id = "Nexus6P";
    private String locale = "en";
    private String orientation = "portrait";
    private String osVersion = "25";

    public Builder setId(String id) {
      this.id = id;
      return this;
    }

    public Builder setLocale(String locale) {
      this.locale = locale;
      return this;
    }

    public Builder setOrientation(String orientation) {
      this.orientation = orientation;
      return this;
    }

    public Builder setOsVersion(String osVersion) {
      this.osVersion = osVersion;
      return this;
    }

    public Device build() {
      return new Device(this.id, this.locale, this.orientation, this.osVersion);
    }
  }

  public static List<Device> parseDevices(String input) {
    List<Device> devices = new ArrayList<>();
    String[] deviceConfigs = input.split(",");

    for (String deviceConfig : deviceConfigs) {

      Builder builder = new Builder();
      String[] properties = deviceConfig.split(";");

      for (String property : properties) {
        String[] keyValue = property.split(":");
        String key = keyValue[0];
        String value = keyValue[1];
        switch (key) {
          case "model":
            builder.setId(value);
            break;
          case "version":
            builder.setOsVersion(value);
            break;
          case "orientation":
            builder.setOrientation(value);
            break;
          case "locale":
            builder.setLocale(value);
            break;

          default:
            break;
        }
      }

      devices.add(builder.build());
    }

    return devices;
  }
}
