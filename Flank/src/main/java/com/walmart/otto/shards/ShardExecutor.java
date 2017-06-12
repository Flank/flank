package com.walmart.otto.shards;

import com.walmart.otto.Constants;
import com.walmart.otto.configurator.Configurator;
import com.walmart.otto.tools.GcloudTool;
import com.walmart.otto.tools.GsutilTool;
import com.walmart.otto.tools.ToolManager;
import com.walmart.otto.utils.FilterUtils;
import com.walmart.otto.utils.XMLUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ShardExecutor {
  ExecutorService executorService;
  ShardCreator shardCreator;
  private Configurator configurator;
  private ToolManager toolManager;

  public ShardExecutor(Configurator configurator, ToolManager toolManager) {
    this.configurator = configurator;
    this.toolManager = toolManager;
    shardCreator = new ShardCreator(configurator);
  }

  public void execute(List<String> testCases, String bucket)
      throws InterruptedException, ExecutionException, IOException {

    executeShards(testCases, bucket);

    if (configurator.isFetchXMLFiles()) {
      fetchResults(toolManager.get(GsutilTool.class));
    }
  }

  public void executeShards(List<String> testCases, String bucket)
      throws InterruptedException, ExecutionException {
    List<Future> futures = new ArrayList<>();
    List<String> shards = shardCreator.getConfigurableShards(testCases);

    if (shards.isEmpty()) {
      shards = shardCreator.getShards(testCases);
    }

    executorService = Executors.newFixedThreadPool(shards.size());

    if (configurator.getShardIndex() != -1) {
      printTests(shards.get(configurator.getShardIndex()), configurator.getShardIndex());
      futures.add(executeShard(shards.get(configurator.getShardIndex()), bucket));
    } else {
      System.out.println(
          shards.size() + " shards will be executed on: " + configurator.getDeviceIds() + "\n");

      for (int i = 0; i < shards.size(); i++) {
        printTests(shards.get(i), i);
        futures.add(executeShard(shards.get(i), bucket));
      }
    }

    executorService.shutdown();

    for (Future future : futures) {
      future.get();
    }
  }

  private Future executeShard(String testCase, String bucket) throws RuntimeException {
    Callable testCaseCallable = getCallable(testCase, bucket);

    return executorService.submit(testCaseCallable);
  }

  private void fetchResults(GsutilTool gsutilTool) throws IOException, InterruptedException {
    Map<String, String> resultsMap = gsutilTool.fetchResults();

    //Add device name to test case names
    resultsMap.forEach(
        (filename, device) ->
            XMLUtils.updateXML(
                Constants.RESULTS_DIR + File.separator + filename, device, "testcase", "name"));
  }

  private void printTests(String testsString, int index) {
    String tests = FilterUtils.filterString(testsString, "class");
    if (tests.length() > 0 && tests.charAt(tests.length() - 1) == ',') {
      tests = tests.substring(0, tests.length() - 1);
    }
    System.out.println("Executing shard " + index + ": " + tests + "\n");
  }

  private Callable<Void> getCallable(String testCase, String bucket) throws RuntimeException {
    Callable<Void> gCloudCallable =
        new Callable<Void>() {
          @Override
          public Void call() throws Exception {
            final GcloudTool gcloudTool = toolManager.get(GcloudTool.class);
            gcloudTool.runGcloud(testCase, bucket);
            return null;
          }
        };
    return gCloudCallable;
  }
}
