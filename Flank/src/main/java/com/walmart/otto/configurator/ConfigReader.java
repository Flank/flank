package com.walmart.otto.configurator;

import java.io.*;
import java.util.Properties;

public class ConfigReader {
  Configurator configurator;
  private String fileName;

  public ConfigReader(String fileName) throws IllegalArgumentException {
    this.fileName = fileName;
    configurator = new Configurator();

    setProperties();
  }

  private void setProperties() throws IllegalArgumentException {
    try (InputStream in = new FileInputStream(fileName)) {
      Properties prop = new Properties();
      prop.load(in);

      for (String property : prop.stringPropertyNames()) {
        setProperty(property, prop.getProperty(property));
      }
      in.close();
    } catch (IOException ignored) {
    }
  }

  public Configurator getConfiguration() {
    return configurator;
  }

  private void setProperty(String property, String value) throws IllegalArgumentException {
    if (value.isEmpty()) {
      return;
    }

    value = value.replaceAll(" ", "");

    switch (property) {
      case "deviceIds":
        configurator.setDeviceIds(value);
        break;

      case "numShards":
        configurator.setNumShards(Integer.parseInt(value));
        break;

      case "shardIndex":
        configurator.setShardIndex(Integer.parseInt(value));
        break;

      case "shard-timeout":
        configurator.setShardTimeout(Integer.parseInt(value));
        break;

      case "shard-duration":
        configurator.setShardDuration(Integer.parseInt(value));
        break;

      case "locales":
        configurator.setLocales(value);
        break;

      case "orientations":
        configurator.setOrientations(value);
        break;

      case "os-version-ids":
        configurator.setOsVersionIds(value);
        break;

      case "debug-prints":
        configurator.setDebug(Boolean.parseBoolean(value));
        break;

      case "gcloud-path":
        configurator.setGcloud(value);
        break;

      case "gsutil-path":
        configurator.setGsutil(value);
        break;

      case "environment-variables":
        configurator.setEnvironmentVariables(value);
        break;

      case "fetch-xml-files":
        configurator.setFetchXMLFiles(Boolean.parseBoolean(value));
        break;

      case "directories-to-pull":
        configurator.setDirectoriesToPull(value);
        break;

      case "fetch-bucket":
        configurator.setFetchBucket(Boolean.parseBoolean(value));
        break;

      default:
        break;
    }
  }
}
