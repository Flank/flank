package cloud_api_poc;

import static cloud_api_poc.Config.getAppApk;
import static cloud_api_poc.Config.getTestApk;
import static cloud_api_poc.GcStorage.uploadApk;
import static cloud_api_poc.TestRunner.pollTests;
import static cloud_api_poc.TestRunner.scheduleTests;

import com.linkedin.dex.parser.DexParser;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

  @SuppressWarnings("unchecked")
  public static void main(String[] args) throws Exception {
    System.out.println("Test runner started.");
    StopWatch stopWatch = new StopWatch().start();

    Path appApk = getAppApk();
    Path testApk = getTestApk();

    String appApkGcsPath = uploadApk(appApk);
    String testApkGcsPath = uploadApk(testApk);
    List<String> testMethodNames = DexParser.findTestNames(testApk.toString());
    testMethodNames = testMethodNames.stream().map(i -> "class " + i).collect(Collectors.toList());

    System.out.println("Running " + testMethodNames.size() + " tests");
    ArrayList testMatrixIds = scheduleTests(appApkGcsPath, testApkGcsPath, testMethodNames);

    boolean allTestsSuccessful = pollTests(testMatrixIds);

    System.out.println("Finished in " + stopWatch.end());

    int exitCode = allTestsSuccessful ? 0 : -1;
    System.exit(exitCode);
  }
}
