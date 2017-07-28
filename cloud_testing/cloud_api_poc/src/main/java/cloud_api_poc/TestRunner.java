package cloud_api_poc;

import static cloud_api_poc.Utils.sleep;
import static java.lang.System.currentTimeMillis;

import com.google.testing.Testing.Projects.TestMatrices.Create;
import com.google.testing.model.AndroidMatrix;
import com.google.testing.model.TestExecution;
import com.google.testing.model.TestMatrix;
import com.google.testing.model.ToolResultsStep;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class TestRunner {
  private TestRunner() {}

  private static final long POLLING_TIMEOUT = 60 * 60 * 1000; // 60m
  private static final int POLLING_INTERVAL = 10 * 1000; // 10s
  private static final String FINISHED = "FINISHED";
  private static final String SUCCESS = "success";

  @SuppressWarnings("unchecked")
  public static boolean pollTests(ArrayList<String> testMatrixIds) {
    long stopTime = currentTimeMillis() + POLLING_TIMEOUT;
    List<String> waitingOnIds = (List<String>) testMatrixIds.clone();
    int matrixCount = testMatrixIds.size();
    List<TestMatrix> finishedTestMatrix = new ArrayList<>(matrixCount);

    boolean invalid = false;
    String lastStatus = "";
    String currentStatus;
    String lastState = "";
    TestExecution lastTestExecution = null;

    while (waitingOnIds.size() > 0) {
      if (currentTimeMillis() > stopTime) {
        throw new RuntimeException(
            "Polling timed out after "
                + TimeUnit.MILLISECONDS.toMinutes(POLLING_TIMEOUT)
                + " minutes");
      }

      currentStatus = finishedTestMatrix.size() + " / " + matrixCount + " complete";
      if (!currentStatus.equals(lastStatus)) {
        System.out.println(currentStatus);
      }
      lastStatus = currentStatus;

      String testMatrixId = waitingOnIds.get(0);
      TestMatrix testMatrix = GcTestMatrix.refresh(testMatrixId);

      boolean finished = true;
      for (TestExecution testExecution : testMatrix.getTestExecutions()) {
        lastState = testExecution.getState();
        System.out.println(testExecution.getState());

        if (!TestExecutionState.isValid(lastState)) {
          invalid = true;
          lastTestExecution = testExecution;
          break;
        }

        if (!FINISHED.equals(lastState)) {
          finished = false;
          break;
        }
      }

      if (invalid) {
        break;
      }

      if (finished) {
        finishedTestMatrix.add(testMatrix);
        waitingOnIds.remove(0);
      } else {
        sleep(POLLING_INTERVAL);
      }
    }

    if (lastState == null) lastState = "";

    if (invalid) {
      // infrastructure errors are transparently retried up to 3 attempts.
      if (lastState.equals(TestExecutionState.ERROR) && lastTestExecution != null) {
        System.out.println(lastTestExecution.getTestDetails().getErrorMessage());
      }
      for (String matrixId : waitingOnIds) {
        GcTestMatrix.cancel(matrixId);
      }

      // Invalid state detected: ERROR is an Infrastructure failure.
      throw new RuntimeException("Invalid state detected: " + lastState);
    }

    System.out.println("Test run finished");
    currentStatus = finishedTestMatrix.size() + " / " + matrixCount + " complete.";
    System.out.println(currentStatus);
    System.out.println();

    System.out.println("Fetching test results...");
    List<ToolResultsValue> results = fetchTestResults(finishedTestMatrix);

    boolean allTestsSuccessful = true;
    long billableMinutes = 0;
    long successCount = 0;
    long failureCount = 0;
    long totalCount = testMatrixIds.size();
    for (ToolResultsValue result : results) {
      System.out.println(result);
      System.out.println();

      if (result.outcome.equals(SUCCESS)) {
        successCount += 1;
      } else {
        allTestsSuccessful = false;
        failureCount += 1;
      }

      billableMinutes += result.billableMinutes;
    }

    System.out.println(
        totalCount + " tests. successful: " + successCount + ", failed: " + failureCount);
    Billing.estimateCosts(billableMinutes);

    return allTestsSuccessful;
  }

  @SuppressWarnings("unchecked")
  private static List<ToolResultsValue> fetchTestResults(List<TestMatrix> finishedTestMatrix) {
    List<ToolResultsStep> steps = new ArrayList<>();
    for (TestMatrix testMatrix : finishedTestMatrix) {
      for (TestExecution testExecution : testMatrix.getTestExecutions()) {
        steps.add(testExecution.getToolResultsStep());
      }
    }

    int shardCount = steps.size();
    Parallel parallel = new Parallel(shardCount);
    List<ToolResultsValue> toolValues = Collections.synchronizedList(new ArrayList(shardCount));

    for (int i = 0; i < shardCount; i++) {
      parallel.addCallable(new ToolResultsCallable(toolValues, steps.get(i)));
    }

    parallel.run();

    if (shardCount != toolValues.size() || shardCount != toolValues.size()) {
      throw new RuntimeException("Synchronization error");
    }

    return toolValues;
  }

  public static ArrayList<String> scheduleTests(
      String appApkGcsPath, String testApkGcsPath, List<String> testMethodNames) {

    int shardCount = testMethodNames.size();

    // *MUST* use synchronized list to play nice with ExecutorService
    List<String> testMatrixIds = Collections.synchronizedList(new ArrayList(shardCount));
    Parallel parallel = new Parallel(shardCount);

    for (String testMethod : testMethodNames) {
      AndroidMatrix androidMatrix = GcAndroidMatrix.build("NexusLowRes", "25", "en", "portrait");

      Create testMatrixCreate =
          GcTestMatrix.build(appApkGcsPath, testApkGcsPath, androidMatrix, testMethod);

      parallel.addCallable(new MatrixCallable(testMatrixIds, testMatrixCreate));
    }

    parallel.run();

    if (shardCount != testMatrixIds.size() || shardCount != testMatrixIds.size()) {
      throw new RuntimeException("Synchronization error");
    }

    return new ArrayList<>(testMatrixIds);
  }
}
