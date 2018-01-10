package com.walmart.otto;

import com.linkedin.dex.parser.DexParser;
import com.linkedin.dex.parser.TestMethod;
import com.walmart.otto.aggregator.HtmlReportGenerationException;
import com.walmart.otto.aggregator.ReportsAggregator;
import com.walmart.otto.aggregator.XmlReportGenerationException;
import com.walmart.otto.configurator.ConfigReader;
import com.walmart.otto.configurator.Configurator;
import com.walmart.otto.filter.TestFilter;
import com.walmart.otto.filter.TestFilters;
import com.walmart.otto.models.Device;
import com.walmart.otto.reporter.PriceReporter;
import com.walmart.otto.reporter.TimeReporter;
import com.walmart.otto.shards.ShardExecutor;
import com.walmart.otto.tools.GcloudTool;
import com.walmart.otto.tools.GsutilTool;
import com.walmart.otto.tools.ProcessExecutor;
import com.walmart.otto.tools.ToolManager;
import com.walmart.otto.utils.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import joptsimple.BuiltinHelpFormatter;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class Flank {

  private ToolManager toolManager;
  private Configurator configurator;
  private GsutilTool gsutilTool;

  public void start(OptionSet options)
      throws RuntimeException, IOException, InterruptedException, ExecutionException {
    long startTime = System.currentTimeMillis();

    String appApk = (String) options.valueOf("a");
    String testApk = (String) options.valueOf("t");
    String config = (String) options.valueOf("c");

    for (String file : new String[] {appApk, testApk}) {
      if (!FileUtils.doFileExist(file)) {
        throw new FileNotFoundException("File not found: " + file);
      }
    }

    configurator = new ConfigReader(config, new Configurator()).getConfiguration();

    toolManager = new ToolManager().load(loadTools(appApk, testApk, configurator));

    if (configurator.getProjectName() == null) {
      configurator.setProjectName(getProjectName(toolManager));
    }

    List<String> testCases = getTestCaseNames(options);

    printShards(configurator, testCases.size());

    gsutilTool = toolManager.get(GsutilTool.class);

    downloadTestTimeFile(gsutilTool, configurator.getShardDuration());

    new ShardExecutor(configurator, toolManager)
        .execute(testCases, gsutilTool.uploadAPKsToBucket());

    gsutilTool.deleteAPKs();

    Optional<File> fetchedBucketDir = gsutilTool.fetchBucket();
    fetchedBucketDir
        .filter(file -> configurator.isAggregateReportsEnabled())
        .ifPresent(this::aggregateTestReports);

    uploadTestTimeFile(gsutilTool, configurator.getShardDuration());

    printEstimates();

    printExecutionTimes(startTime);
  }

  private void aggregateTestReports(File dir) {
    try {
      new ReportsAggregator(configurator, gsutilTool).aggregate(dir.toPath());
    } catch (XmlReportGenerationException
        | HtmlReportGenerationException
        | IOException
        | InterruptedException e) {
      e.printStackTrace(); // don't fail the build for the reports
    }
  }

  public static void main(String[] args) throws IOException {
    OptionParser parser = getOptionParser();
    OptionSet options = parser.parse(args);
    if (options.has("h")) {
      parser.printHelpOn(System.out);
      System.exit(-1);
    }

    Flank flank = new Flank();

    try {
      flank.start(options);
      if (flank.hasTestFailed()) {
        System.exit(-1);
      }
    } catch (RuntimeException | IOException | InterruptedException | ExecutionException e) {
      exitWithFailure(e);
    }
  }

  static OptionParser getOptionParser() {
    OptionParser optionParser =
        new OptionParser() {
          {
            accepts("h").forHelp();

            accepts("a", Constants.APP_APK_OPTION_DESCRIPTION)
                .withRequiredArg()
                .ofType(String.class)
                .required()
                .describedAs("app.apk");

            accepts("t", Constants.TEST_APK_OPTION_DESCRIPTION)
                .withRequiredArg()
                .ofType(String.class)
                .required()
                .describedAs("app-androidTest.apk");

            accepts("c", Constants.CONFIG_FILE_OPTION_DESCRIPTION)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("config.properties")
                .defaultsTo(Constants.CONFIG_PROPERTIES);

            accepts("f", Constants.TEST_FILTERS_DESCRIPTION)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("option1; option2;...");
          }
        };

    optionParser.formatHelpWith(new BuiltinHelpFormatter(120, 2));

    return optionParser;
  }

  private static void exitWithFailure(Exception e) {
    e.printStackTrace();
    System.exit(-1);
  }

  private boolean hasTestFailed() {
    return toolManager.get(GcloudTool.class).hasTestFailed();
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
          "\nLocal 'flank.tests' found. It contains test execution times used to create shards with configurable durations. Default shard duration is 120 seconds.\n");
    } else if (!new File(Constants.TEST_TIME_FILE).exists()) {
      if (gsutilTool.findTestTimeFile()) {
        System.out.println(
            "\nDownloading 'flank.tests'. It contains test execution times used to create shards with configurable durations. Default shard duration is 120 seconds.\n");
        gsutilTool.downloadTestTimeFile();
      }
    }
  }

  private void uploadTestTimeFile(GsutilTool gsutilTool, int shardDuration)
      throws IOException, InterruptedException {
    if (shardDuration == -1 || !new File(Constants.TEST_TIME_FILE).exists()) {
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
              + configurator
                  .getDevices()
                  .stream()
                  .map(Device::getId)
                  .reduce((s, s2) -> s + ", " + s2));
      return;
    }
  }

  private List<String> getTestCaseNames(OptionSet options) {
    System.setOut(getEmptyStream());
    List<TestMethod> testMethods = DexParser.findTestMethods((String) options.valueOf("t"));
    System.setOut(originalStream);

    final TestFilter filter;
    if (options.has("f")) {
      filter = TestFilters.fromCommandLineArguments((String) options.valueOf("f"));
    } else {
      filter = TestFilters.createDefault();
    }

    List<String> filteredTests =
        testMethods
            .stream()
            .filter(filter::shouldRun)
            .map(TestMethod::getTestName)
            .collect(Collectors.toList());

    if (filteredTests.size() == 0) {
      throw new IllegalStateException("No tests to run!");
    }

    System.out.printf("Running %d out of %d tests", filteredTests.size(), testMethods.size());

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
