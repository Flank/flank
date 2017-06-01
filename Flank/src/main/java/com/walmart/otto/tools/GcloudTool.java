package com.walmart.otto.tools;

import com.walmart.otto.reporter.TimeReporter;
import com.walmart.otto.utils.FileUtils;
import com.walmart.otto.utils.FilterUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class GcloudTool extends Tool {
    boolean printTests = true;

    private int latestExecutionTime;

    public GcloudTool(ToolManager.Config config) {
        super(ToolManager.GCLOUD_TOOL, config);
    }

    private String quote(String arg) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(arg).append("\"");
        return sb.toString();
    }

    public void runGcloud(String testCase, String bucket) {
        String[] runGcloud = new String[]{
                getConfigurator().getGcloud(), "firebase", "test", "android", "run",
                "--type", quote("instrumentation"),
                "--app", quote(bucket + getSimpleName(getAppAPK())),
                "--test", quote(bucket + getSimpleName(getTestAPK())),
                "--results-bucket", quote("gs://" + bucket.split("/")[2]),
                "--device-ids", quote(getConfigurator().getDeviceIds()),
                "--os-version-ids", quote(getConfigurator().getOsVersionIds()),
                "--locales", quote(getConfigurator().getLocales()),
                "--orientations", quote(getConfigurator().getOrientations()),
                "--timeout", quote(getConfigurator().getShardTimeout() + "m"),
                "--results-dir", quote(bucket.split("/")[3]),
                "--test-targets", quote(testCase)};

        List<String> gcloudList = new ArrayList<>(Arrays.asList(runGcloud));

        String envVars = getConfigurator().getEnvironmentVariables();
        if (!envVars.isEmpty()) {
            gcloudList.add("--environment-variables");
            gcloudList.add(envVars);
        }

        executeGcloud(gcloudList.toArray(new String[0]));
    }

    public void executeGcloud(String[] commands) {
        List<String> inputStreamList = new ArrayList<>();
        List<String> errorStreamList = new ArrayList<>();

        String resultsLink = null;

        executeCommand(commands, inputStreamList, errorStreamList);

        for (String line : errorStreamList) {
            if (line.contains("More details are available")) {
                resultsLink = line;
            } else if (line.contains("Test time=")) {
                String[] timeLine = line.split(Pattern.quote("time="));
                latestExecutionTime = Integer.parseInt(timeLine[1].replaceAll("\\D+", ""));
                TimeReporter.addExecutionTime(Integer.parseInt(timeLine[1].replaceAll("\\D+", "")));
            }
        }

        for (String line : inputStreamList) {
            if (printTests) {
                printTests(commands);
            }

            System.out.println(line);
        }
        if (resultsLink != null) {
            System.out.println("\n" + resultsLink + "\n");
        }
        printTests = true;
    }

    public String getProjectName(){
        String[] projectDetails = new String[]{getConfigurator().getGcloud(), "config", "get-value", "project"};
        List<String> inputStreamList = new ArrayList<>();
        String projectName = "";
        executeCommand(projectDetails, inputStreamList, new ArrayList<>());

        for(String projectProperties : inputStreamList){
            projectName = projectProperties;
        }
        return projectName;

    }

    private void printTests(String[] commands) {
        String tests = FilterUtils.filterString(commands[commands.length - 1], "class");

        if (tests.charAt(tests.length() - 1) == ',') {
            tests = tests.substring(0, tests.length() - 1);

            if (!tests.contains(",")) {
                //Save test case name and execution times
                FileUtils.writeToShardFile(tests, String.valueOf(latestExecutionTime));
            }
        }
        System.out.println("Test(s):" + tests);
        printTests = false;
    }

    private String getSimpleName(String file) {
        String[] parts = file.split(File.separator);
        return parts[parts.length - 1];
    }

}
