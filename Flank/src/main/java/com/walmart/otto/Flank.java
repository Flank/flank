package com.walmart.otto;

import com.linkedin.dex.parser.DexParser;
import com.walmart.otto.configurator.ConfigReader;
import com.walmart.otto.configurator.Configurator;
import com.walmart.otto.reporter.PriceReporter;
import com.walmart.otto.reporter.TimeReporter;
import com.walmart.otto.shards.ShardExecutor;
import com.walmart.otto.tools.GcloudTool;
import com.walmart.otto.tools.GsutilTool;
import com.walmart.otto.tools.ProcessExecutor;
import com.walmart.otto.tools.ToolManager;
import com.walmart.otto.utils.FileUtils;
import com.walmart.otto.utils.FilterUtils;
import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Flank {
  private ToolManager toolManager;
  private Configurator configurator;

  public void start(String[] args)
      throws RuntimeException, IOException, InterruptedException, ExecutionException {
    long startTime = System.currentTimeMillis();

    if (!FileUtils.doFileExist(args[0])) {
      throw new FileNotFoundException("File not found: " + args[0]);
    }

    if (!FileUtils.doFileExist(args[1])) {
      throw new FileNotFoundException("File not found: " + args[1]);
    }

    configurator = new ConfigReader(Constants.CONFIG_PROPERTIES).getConfiguration();

    toolManager = new ToolManager().load(loadTools(args[0], args[1], configurator));

    configurator.setProjectName(getProjectName(toolManager));

    List<String> testCases = getTestCaseNames(args);

    if (testCases.size() == 0) {
      throw new IllegalArgumentException("No tests found within the specified package!");
    }

    printShards(configurator, testCases.size());

    GsutilTool gsutilTool = toolManager.get(GsutilTool.class);

    downloadTestTimeFile(gsutilTool, configurator.getShardDuration());

    new ShardExecutor(configurator, toolManager)
        .execute(testCases, gsutilTool.uploadAPKsToBucket());

    gsutilTool.deleteAPKs();

    uploadTestTimeFile(gsutilTool, configurator.getShardDuration());

    printEstimates();

    printExecutionTimes(startTime);
  }

  public static void main(String[] args) {
    Flank flank = new Flank();

    try {
      if (validateArguments(args)) {
        flank.start(args);
      }
    } catch (RuntimeException e) {
      exitWithFailure(e);
    } catch (IOException e) {
      exitWithFailure(e);
    } catch (InterruptedException e) {
      exitWithFailure(e);
    } catch (ExecutionException e) {
      exitWithFailure(e);
    }
  }

  private static void exitWithFailure(Exception e) {
    e.printStackTrace();
    System.exit(-1);
  }

  private ToolManager.Config loadTools(String appAPK, String testAPK, Configurator configurator) {
    ToolManager.Config toolConfig = new ToolManager.Config();

    toolConfig.appAPK = appAPK;
    toolConfig.testAPK = testAPK;
    toolConfig.configurator = configurator;
    toolConfig.processExecutor = new ProcessExecutor(configurator);

    return toolConfig;
  }

  private void printExecutionTimes(long startTime) {
    System.out.println(
        "\n\n["
            + TimeReporter.getEndTime()
            + "] Total time: "
            + TimeReporter.getTotalTime(startTime)
            + "\n");
  }

  private void printEstimates() {
    System.out.println(
        "\nBillable time: "
            + PriceReporter.getTotalBillableTime(TimeReporter.getExecutionTimes())
            + " min(s) \n");
    HashMap<String, BigDecimal> prices =
        PriceReporter.getTotalPrice(TimeReporter.getExecutionTimes());
    System.out.print("Estimated cost: ");
    for (Map.Entry<String, BigDecimal> price : prices.entrySet()) {
      System.out.print("$" + price.getValue() + "(" + price.getKey() + ") ");
    }
  }

  private void downloadTestTimeFile(GsutilTool gsutilTool, int shardDuration)
      throws IOException, InterruptedException {
    if (shardDuration == -1) {
      return;
    }

    if (new File(Constants.TEST_TIME_FILE).exists()) {
      System.out.println(
          "\nLocal 'flank.tests' found. It contains test execution times used to create shards with configurable durations. Default shard duration is 120 seconds.");
    } else if (!new File(Constants.TEST_TIME_FILE).exists()) {
      if (gsutilTool.findTestTimeFile()) {
        System.out.println(
            "\nDownloading 'flank.tests'. It contains test execution times used to create shards with configurable durations. Default shard duration is 120 seconds.");
        gsutilTool.downloadTestTimeFile();
      }
    }
  }

  private void uploadTestTimeFile(GsutilTool gsutilTool, int shardDuration)
      throws IOException, InterruptedException {
    if (shardDuration == -1) {
      return;
    }
    gsutilTool.uploadTestTimeFile();
  }

  private String getProjectName(ToolManager toolManager) throws IOException, InterruptedException {
    System.setOut(getEmptyStream());

    String text = toolManager.get(GcloudTool.class).getProjectName() + "-flank";

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

  private void printShards(Configurator configurator, int numberOfShards) {
    int numShards = configurator.getNumShards();

    if (configurator.getShardIndex() != -1) {
      if (numShards != -1) {
        numberOfShards = numShards;
      }
      System.out.println(
          "\nShard with index: "
              + configurator.getShardIndex()
              + " ("
              + numberOfShards
              + " shards in total) will be executed on: "
              + configurator.getDeviceIds());
      return;
    }
  }

  private List<String> getTestCaseNames(String[] args) {
    System.setOut(getEmptyStream());
    List<String> filteredTests;

    if (args.length < 3) {
      filteredTests = DexParser.findTestNames(args[1]);
    } else {
      filteredTests = FilterUtils.filterTests(DexParser.findTestNames(args[1]), args[2]);
    }

    System.setOut(originalStream);
    return filteredTests;
  }

  static PrintStream originalStream = System.out;

  public PrintStream getEmptyStream() {
    PrintStream emptyStream = null;
    try {
      emptyStream =
          new PrintStream(
              new OutputStream() {
                public void write(int b) {}
              },
              false,
              "UTF-8");
    } catch (UnsupportedEncodingException ignored) {
    }
    return emptyStream;
  }
}
