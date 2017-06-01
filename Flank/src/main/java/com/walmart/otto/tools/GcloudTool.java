package com.walmart.otto.tools;

import com.walmart.otto.reporter.TimeReporter;
import com.walmart.otto.utils.FileUtils;
import com.walmart.otto.utils.FilterUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.walmart.otto.utils.FileUtils.getSimpleName;

public class GcloudTool extends Tool {
    boolean printTests = true;

    private int latestExecutionTime;

    public GcloudTool(ToolManager.Config config) {
        super(ToolManager.GCLOUD_TOOL, config);
    }

    public void runGcloud(String testCase, String bucket) {
        // don't quote arguments or ProcessBuilder will error in strange ways.
        String[] runGcloud = new String[]{
                getConfigurator().getGcloud(), "firebase", "test", "android", "run",
                "--type", "instrumentation",
                "--app", bucket + getSimpleName(getAppAPK()),
                "--test", bucket + getSimpleName(getTestAPK()),
                "--results-bucket", "gs://" + bucket.split("/")[2],
                "--device-ids", getConfigurator().getDeviceIds(),
                "--os-version-ids", getConfigurator().getOsVersionIds(),
                "--locales", getConfigurator().getLocales(),
                "--orientations", getConfigurator().getOrientations(),
                "--timeout", getConfigurator().getShardTimeout() + "m",
                "--results-dir", bucket.split("/")[3],
                "--test-targets", testCase};

        List<String> gcloudList = new ArrayList<>(Arrays.asList(runGcloud));

        String envVars = getConfigurator().getEnvironmentVariables();
        if (!envVars.isEmpty()) {
            gcloudList.add("--environment-variables");
            gcloudList.add(envVars);
        }
        String[] cmdArray = gcloudList.toArray(new String[0]);

        executeGcloud(cmdArray);
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

    public String getProjectName() {
        String[] projectDetails = new String[]{getConfigurator().getGcloud(), "config", "get-value", "project"};
        List<String> inputStreamList = new ArrayList<>();
        String projectName = "";
        executeCommand(projectDetails, inputStreamList, new ArrayList<>());

        for (String projectProperties : inputStreamList) {
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
}
