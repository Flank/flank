package com.walmart.otto.configurator;

public class Configurator {
    private String deviceIds = "Nexus6P";
    private String locales = "en";
    private String orientations = "portrait";
    private String osVersionIds = "23";
    private String gcloud = "gcloud";
    private String gsutil = "gsutil";
    private String environmentVariables = "";
    private String projectNameHash;

    private boolean fetchXMLFiles = true;
    private boolean debug = false;
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

    public void setGcloud(String gcloud) {
        this.gcloud = gcloud;
    }

    public String getGsutil() {
        return gsutil;
    }

    public void setGsutil(String gsutil) {
        this.gsutil = gsutil;
    }

    public int getShardTimeout() {
        return shardTimeout;
    }

    public void setShardTimeout(int shardTimeout) {
        this.shardTimeout = shardTimeout;
    }

    public int getShardDuration() {
        return shardDuration;
    }

    public void setShardDuration(int shardDuration) {
        this.shardDuration = shardDuration;
    }

    public String getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(String environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public String getTestTimeBucket() {
        return "gs://" + getProjectNameHash() + "/";
    }

    public String getProjectNameHash() {
        return projectNameHash;
    }

    public void setProjectNameHash(String projectNameHash) {
        this.projectNameHash = projectNameHash;
    }

    public boolean isFetchXMLFiles() {
        return fetchXMLFiles;
    }

    public void setFetchXMLFiles(boolean fetchXMLFiles) {
        this.fetchXMLFiles = fetchXMLFiles;
    }
}