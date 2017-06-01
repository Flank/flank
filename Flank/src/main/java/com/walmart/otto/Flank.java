package com.walmart.otto;

import com.linkedin.dex.parser.DexParser;
import com.walmart.otto.configurator.Configurator;
import com.walmart.otto.configurator.ConfigReader;
import com.walmart.otto.reporter.TimeReporter;
import com.walmart.otto.shards.ShardExecutor;
import com.walmart.otto.tools.GcloudTool;
import com.walmart.otto.tools.GsutilTool;
import com.walmart.otto.tools.ProcessExecutor;
import com.walmart.otto.tools.ToolManager;
import com.walmart.otto.utils.FilterUtils;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Flank {
    private static ToolManager toolManager;
    private static Configurator configurator;

    public static void main(String[] args) {
        if (!validateArguments(args) || !doFilesExist(args[0], args[1])) {
            return;
        }

        configurator = new ConfigReader(Constants.CONFIG_PROPERTIES).getConfiguration();

        loadTools(args[0], args[1], configurator);

        configurator.setProjectName(getProjectName());

        List<String> testCases = getTestCaseNames(args);

        if (testCases.size() == 0) {
            System.out.println("No tests found within the specified package!\n");
            return;
        }

        printShards(configurator, testCases.size());

        GsutilTool gsutilTool = toolManager.get(GsutilTool.class);

        downloadTestTimeFile(gsutilTool);

        new ShardExecutor(configurator, toolManager).execute(testCases, gsutilTool.uploadAPKsToBucket());

        gsutilTool.deleteAPKs();

        uploadTestTimeFile(gsutilTool);

        printExecutionTimes();
    }

    private static void loadTools(String appAPK, String testAPK, Configurator configurator) {
        ToolManager.Config toolConfig = new ToolManager.Config();

        toolConfig.appAPK = appAPK;
        toolConfig.testAPK = testAPK;
        toolConfig.configurator = configurator;
        toolConfig.processExecutor = new ProcessExecutor(configurator);


        toolManager = new ToolManager().load(toolConfig);
    }

    private static void printExecutionTimes() {
        System.out.println("Combined test execution time: ~" + TimeUnit.SECONDS.toMinutes(TimeReporter.getCombinedExecutionTimes()) + " minutes\n");
        System.out.println("End time: " + TimeReporter.getEndTime() + "\n");
    }

    private static void downloadTestTimeFile(GsutilTool gsutilTool) {
        if(configurator.getShardDuration() == -1){
            return;
        }

        if (new File(Constants.TEST_TIME_FILE).exists()) {
            System.out.println("\nLocal 'flank.tests' found. It contains test execution times used to create shards with configurable durations. Default shard duration is 120 seconds.");
        } else if (!new File(Constants.TEST_TIME_FILE).exists()) {
            if (gsutilTool.findTestTimeFile()) {
                System.out.println("\nDownloading 'flank.tests'. It contains test execution times used to create shards with configurable durations. Default shard duration is 120 seconds.");
                gsutilTool.downloadTestTimeFile();
            }
        }
    }

    private static void uploadTestTimeFile(GsutilTool gsutilTool){
        if(configurator.getShardDuration() == -1){
            return;
        }

        gsutilTool.uploadTestTimeFile();
    }

    private static String getProjectName(){
        System.setOut(emptyStream);
        String text = String.valueOf(toolManager.get(GcloudTool.class).getProjectName()) + "-flank";
        System.setOut(originalStream);
        return text;
    }

    private static boolean validateArguments(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: Flank <app-apk> <test-apk> [package-name]");
            return false;
        }
        return true;
    }

    private static void printShards(Configurator configurator, int numberOfShards) {
        int numShards = configurator.getNumShards();

        if (configurator.getShardIndex() != -1) {
            if (numShards != -1) {
                numberOfShards = numShards;
            }
            System.out.println("\nShard with index: " + configurator.getShardIndex() + " (" + numberOfShards + " shards in total) will be executed on: " + configurator.getDeviceIds() + "\n");
            return;
        }
    }

    private static boolean doFilesExist(String appAPK, String testAPK) {
        if (!new File(appAPK).exists()) {
            System.out.println("File: " + appAPK + " can not be found!");
            return false;
        } else if (!new File(testAPK).exists()) {
            System.out.println("File: " + testAPK + " can not be found!");
            return false;
        }
        return true;
    }

    private static List<String> getTestCaseNames(String[] args) {
        System.setOut(emptyStream);
        List<String> filteredTests;

        if(args.length < 3) {
            filteredTests = DexParser.findTestNames(args[1]);
        }

        else{
            filteredTests = FilterUtils.filterTests(DexParser.findTestNames(args[1]), args[2]);
        }

        System.setOut(originalStream);
        return filteredTests;
    }

    static PrintStream originalStream = System.out;
    static PrintStream emptyStream = new PrintStream(new OutputStream() {
        public void write(int b) {
        }
    });

}
