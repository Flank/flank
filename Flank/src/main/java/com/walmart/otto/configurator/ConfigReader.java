package com.walmart.otto.configurator;

import java.io.*;
import java.util.Properties;

public class ConfigReader {
    Configurator configurator;
    private String fileName;

    public ConfigReader(String fileName) {
        this.fileName = fileName;
        configurator = new Configurator();

        setProperties();
    }

    private void setProperties() {
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

    private void setProperty(String property, String value) {
        if (value.isEmpty()) {
            return;
        }

        value = value.replaceAll(" ","");

        switch (property) {
            case "deviceIds":
                configurator.setDeviceIds(value);
                break;

            case "numShards":
                try {
                    configurator.setNumShards(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;

            case "shardIndex":
                try {
                    configurator.setShardIndex(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;

            case "shard-timeout":
                try {
                    configurator.setShardTimeout(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;

            case "shard-duration":
                try {
                    configurator.setShardDuration(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
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

            case "fetch-xml-files":
                configurator.setFetchXMLFiles(Boolean.parseBoolean(value));
        }
    }


}
