package com.walmart.otto.configurator;

import java.io.File;

public class Configurator {
  private String deviceIds = "Nexus6P";
  private String locales = "en";
  private String orientations = "portrait";
  private String osVersionIds = "25";
  private String gcloud = "gcloud";
  private String gsutil = "gsutil";
  private String environmentVariables = "";
  private String projectName;
  private String directoriesToPull = "";

  private boolean fetchXMLFiles = true;
  private boolean debug = false;
  private boolean fetchBucket = false;
  private int numShards = -1;
  private int shardIndex = -1;
  private int shardTimeout = 5;
  private int shardDuration = 120;

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

  public String getDeviceIds() {
    return deviceIds;
  }

  public void setDeviceIds(String deviceIds) {
    this.deviceIds = deviceIds;
  }

  public String getOsVersionIds() {
    return osVersionIds;
  }

  public void setOsVersionIds(String osVersionIds) {
    this.osVersionIds = osVersionIds;
  }

  public String getLocales() {
    return locales;
  }

  public void setLocales(String locales) {
    this.locales = locales;
  }

  public String getOrientations() {
    return orientations;
  }

  public void setOrientations(String orientations) {
    this.orientations = orientations;
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
}
