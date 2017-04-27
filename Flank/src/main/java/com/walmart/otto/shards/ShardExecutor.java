package com.walmart.otto.shards;

import com.walmart.otto.Constants;
import com.walmart.otto.configurator.Configurator;
import com.walmart.otto.tools.GcloudTool;
import com.walmart.otto.tools.GsutilTool;
import com.walmart.otto.tools.ToolManager;
import com.walmart.otto.utils.FilterUtils;
import com.walmart.otto.utils.XMLUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    public void execute(List<String> testCases, String bucket) {

        executeShards(testCases, bucket);

        fetchResults(toolManager.get(GsutilTool.class));

    }

    public void executeShards(List<String> testCases, String bucket) {
        List<String> shards = shardCreator.getConfigurableShards(testCases);

        if (shards.isEmpty()) {
            shards = shardCreator.getShards(testCases);
        }

        executorService = Executors.newFixedThreadPool(shards.size());

        if (configurator.getShardIndex() != -1) {
            printTests(shards.get(configurator.getShardIndex()), configurator.getShardIndex());
            executeShard(shards.get(configurator.getShardIndex()), bucket);
        } else {
            System.out.println(shards.size() + " shards will be executed on: " + configurator.getDeviceIds() + "\n");

            for (int i = 0; i < shards.size(); i++) {
                printTests(shards.get(i), i);
                executeShard(shards.get(i), bucket);
            }
        }
        executorService.shutdown();

        try {
            executorService.awaitTermination(240, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void executeShard(String testCase, String bucket) {
        Runnable testCaseRunner = getRunnable(testCase, bucket);

        executorService.submit(testCaseRunner);
    }

    private void fetchResults(GsutilTool gsutilTool){
        Map<String, String> resultsMap = gsutilTool.fetchResults();

        gsutilTool.deleteAPKs();

        //Add device name to test case names
        resultsMap.forEach((filename, device) -> XMLUtils.updateXML(Constants.RESULTS_DIR + File.separator + filename, device, "testcase", "name"));
    }

    private void printTests(String testsString, int index) {
        String tests = FilterUtils.filterString(testsString, "class");
        if (tests.length() > 0 && tests.charAt(tests.length() - 1) == ',') {
            tests = tests.substring(0, tests.length() - 1);
        }
        System.out.println("Executing shard " + index + ": " + tests + "\n");
    }

    private Runnable getRunnable(String testCase, String bucket) {
        Runnable gCloudRunner = new Runnable() {
            @Override
            public void run() {

                final GcloudTool gcloudTool = toolManager.get(GcloudTool.class);

                gcloudTool.runGcloud(testCase, bucket);
            }
        };
        return gCloudRunner;
    }

}
