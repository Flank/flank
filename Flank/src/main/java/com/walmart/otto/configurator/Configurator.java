package com.walmart.otto.configurator;

import com.walmart.otto.models.Device;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Configurator {

  private List<String> deviceIds = new ArrayList<>(Collections.singletonList("Nexus6P"));
  private List<String> locales = new ArrayList<>(Collections.singletonList("en"));
  private List<String> orientations = new ArrayList<>(Collections.singletonList("portrait"));
  private List<String> osVersionIds = new ArrayList<>(Collections.singletonList("25"));
  private List<Device> devices = new ArrayList<>();
  private String gcloud = "gcloud";
  private String gsutil = "gsutil";
  private String environmentVariables = "";
  private String projectName;
  private String directoriesToPull = "";

  private boolean useOrchestrator = false;
  private boolean useGCloudBeta = false;
  private boolean fetchXMLFiles = true;
  private boolean debug = false;
  private boolean fetchBucket = false;
  private boolean autoGoogleLogin = true;
  private boolean recordVideo = true;
  private boolean recordPerformanceMetrics = true;

  private int numShards = -1;
  private int shardIndex = -1;
  private int shardTimeout = 5;
  private int shardDuration = 120;

  private boolean aggregateReportsEnabled = false;
  private boolean generateAggregatedXmlReport = true;
  private boolean generateAggregatedHtmlReport = true;
  private boolean generateSplitVideo = false;

  public Configurator() {
    setupDevices();
  }

  public int getNumShards() {
    return numShards;
  }

  public void setNumShards(int numShards) {
    this.numShards = numShards;
  }

  public int getShardIndex() {
    return shardIndex;
  }

  public void setShardIndex(int shardIndex) {
    this.shardIndex = shardIndex;
  }

  @Deprecated
  public void setDeviceIds(String deviceIds) {
    this.deviceIds = Arrays.asList(deviceIds.split(","));
    setupDevices();
  }

  @Deprecated
  public void setOsVersionIds(String osVersionIds) {
    this.osVersionIds = Arrays.asList(osVersionIds.split(","));
    setupDevices();
  }

  @Deprecated
  public void setLocales(String locales) {
    this.locales = Arrays.asList(locales.split(","));
    setupDevices();
  }

  @Deprecated
  public void setOrientations(String orientations) {
    this.orientations = Arrays.asList(orientations.split(","));
    setupDevices();
  }

  public void addDevices(List<Device> devices) {
    this.devices = devices;
  }

  public List<Device> getDevices() {
    return devices;
  }

  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public String getGcloud() {
    return gcloud;
  }

  public void setGcloud(String gcloud) throws IllegalArgumentException {
    if (!new File(gcloud).exists()) {
      throw new IllegalArgumentException("Gcloud binary not found at provided path: " + gcloud);
    }
    this.gcloud = gcloud;
  }

  public String getGsutil() {
    return gsutil;
  }

  public void setGsutil(String gsutil) throws IllegalArgumentException {
    if (!new File(gsutil).exists()) {
      throw new IllegalArgumentException("Gsutil binary not found at provided path: " + gsutil);
    }
    this.gsutil = gsutil;
  }

  public int getShardTimeout() {
    return shardTimeout;
  }

  public void setShardTimeout(int shardTimeout) throws NumberFormatException {
    this.shardTimeout = shardTimeout;
  }

  public int getShardDuration() {
    return shardDuration;
  }

  public void setShardDuration(int shardDuration) throws NumberFormatException {
    this.shardDuration = shardDuration;
  }

  public String getEnvironmentVariables() {
    return environmentVariables;
  }

  public void setEnvironmentVariables(String environmentVariables) {
    this.environmentVariables = environmentVariables;
  }

  public String getTestTimeBucket() {
    return "gs://" + getProjectName() + "/";
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public boolean isFetchXMLFiles() {
    return fetchXMLFiles;
  }

  public void setFetchXMLFiles(boolean fetchXMLFiles) {
    this.fetchXMLFiles = fetchXMLFiles;
  }

  public String getDirectoriesToPull() {
    return directoriesToPull;
  }

  public void setDirectoriesToPull(String directoriesToPull) {
    this.directoriesToPull = directoriesToPull;
  }

  public boolean isFetchBucket() {
    return fetchBucket;
  }

  public void setFetchBucket(boolean fetchArtefacts) {
    this.fetchBucket = fetchArtefacts;
  }

  public boolean isUseOrchestrator() {
    return useOrchestrator;
  }

  public void setUseOrchestrator(boolean useOrchestrator) {
    this.useOrchestrator = useOrchestrator;
  }

  public boolean isUseGCloudBeta() {
    return useGCloudBeta;
  }

  public void setUseGCloudBeta(boolean useGCloudBeta) {
    this.useGCloudBeta = useGCloudBeta;
  }

  public boolean autoGoogleLogin() {
    return autoGoogleLogin;
  }

  public void setAutoGoogleLogin(boolean autoGoogleLogin) {
    this.autoGoogleLogin = autoGoogleLogin;
  }

  public boolean recordVideo() {
    return recordVideo;
  }

  public void setRecordVideo(boolean recordVideo) {
    this.recordVideo = recordVideo;
  }

  public boolean recordPerformanceMetrics() {
    return recordPerformanceMetrics;
  }

  public void setRecordPerformanceMetrics(boolean recordPerformanceMetrics) {
    this.recordPerformanceMetrics = recordPerformanceMetrics;
  }

  private void setupDevices() {
    devices = new ArrayList<>();
    for (String locale : locales) {
      for (String deviceId : deviceIds) {
        for (String osVersion : osVersionIds) {
          for (String orientation : orientations) {
            devices.add(
                new Device.Builder()
                    .setId(deviceId)
                    .setLocale(locale)
                    .setOrientation(orientation)
                    .setOsVersion(osVersion)
                    .build());
          }
        }
      }
    }
  }

  public void setAggregateReportsEnabled(boolean aggregateReportsEnabled) {
    this.aggregateReportsEnabled = aggregateReportsEnabled;
  }

  public boolean isAggregateReportsEnabled() {
    return aggregateReportsEnabled;
  }

  public boolean isGenerateAggregatedXmlReport() {
    return generateAggregatedXmlReport;
  }

  public void setGenerateAggregatedXmlReport(boolean generateAggregatedXmlReport) {
    this.generateAggregatedXmlReport = generateAggregatedXmlReport;
  }

  public boolean isGenerateAggregatedHtmlReport() {
    return generateAggregatedHtmlReport;
  }

  public void setGenerateAggregatedHtmlReport(boolean generateAggregatedHtmlReport) {
    this.generateAggregatedHtmlReport = generateAggregatedHtmlReport;
  }

  public boolean isGenerateSplitVideo() {
    return generateSplitVideo;
  }

  public void setGenerateSplitVideo(boolean generateSplitVideo) {
    this.generateSplitVideo = generateSplitVideo;
  }
}
