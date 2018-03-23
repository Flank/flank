package com.walmart.otto.configurator;

import com.walmart.otto.models.Device;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

  private Configurator configurator;
  private String fileName;

  public ConfigReader(String fileName, Configurator configurator) throws IllegalArgumentException {
    this.fileName = fileName;
    this.configurator = configurator;

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

  @SuppressWarnings("deprecation")
  private void setProperty(String property, String value) throws IllegalArgumentException {
    if (value.isEmpty()) {
      return;
    }

    value = value.replaceAll(" ", "");

    switch (property) {
      case "devices":
        configurator.addDevices(Device.parseDevices(value));
        break;

      case "deviceIds":
        configurator.setDeviceIds(value);
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

      case "debug-prints":
        configurator.setDebug(Boolean.parseBoolean(value));
        break;

      case "gcloud-path":
        configurator.setGcloud(value);
        break;

      case "gsutil-path":
        configurator.setGsutil(value);
        break;

      case "gcloud-bucket":
        configurator.setProjectName(value);
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

      case "use-orchestrator":
        configurator.setUseOrchestrator(Boolean.parseBoolean(value));
        break;

      case "use-gcloud-beta":
        configurator.setUseGCloudBeta(Boolean.parseBoolean(value));
        break;

      case "aggregate-reports.enabled":
        configurator.setAggregateReportsEnabled(Boolean.parseBoolean(value));
        break;

      case "aggregate-reports.xml":
        configurator.setGenerateAggregatedXmlReport(Boolean.parseBoolean(value));
        break;

      case "aggregate-reports.html":
        configurator.setGenerateAggregatedHtmlReport(Boolean.parseBoolean(value));
        break;

      case "aggregate-reports.split-video":
        configurator.setGenerateSplitVideo(Boolean.parseBoolean(value));
        break;

      case "auto-google-login":
        configurator.setAutoGoogleLogin(Boolean.parseBoolean(value));
        break;

      case "record-video":
        configurator.setRecordVideo(Boolean.parseBoolean(value));
        break;

      case "record-performance-metrics":
        configurator.setRecordPerformanceMetrics(Boolean.parseBoolean(value));
        break;

      default:
        break;
    }
  }
}
