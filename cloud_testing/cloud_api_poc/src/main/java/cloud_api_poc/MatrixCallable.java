package cloud_api_poc;

import static cloud_api_poc.Utils.fatalError;
import static cloud_api_poc.Utils.sleep;

import com.google.testing.Testing.Projects.TestMatrices.Create;
import com.google.testing.model.TestMatrix;
import java.util.List;
import java.util.concurrent.Callable;

class MatrixCallable implements Callable {
  private List<String> testMatrixIds;
  private Create testMatrixCreate;

  MatrixCallable(List<String> testMatrixIds, Create testMatrixCreate) {
    this.testMatrixIds = testMatrixIds;
    this.testMatrixCreate = testMatrixCreate;
  }

  /** Returns test matrix id */
  private String executeTestMatrix() {
    TestMatrix testMatrix = null;
    try {
      testMatrix = testMatrixCreate.execute();
    } catch (Exception e) {
      try {
        testMatrix = testMatrixCreate.execute();
      } catch (Exception e2) {
        try {
          testMatrix = testMatrixCreate.execute();
        } catch (Exception e3) {
          fatalError(e3);
        }
      }
    }

    if (testMatrix == null) {
      throw new IllegalStateException("test matrix is null");
    }

    return testMatrix.getTestMatrixId();
  }

  private void run() {
    int tryCount = 3;

    String testMatrixId = null;
    boolean infrastructureFailure = false;
    for (int i = 0; i < tryCount; i++) {
      testMatrixId = executeTestMatrix();

      while (TestMatrixState.validatingOrPending(testMatrixId)) {
        sleep(20 * 1000);
      }

      if (TestMatrixState.infastructureFailure(testMatrixId)) {
        System.out.println("Retrying infrastructure failure");
        infrastructureFailure = true;
      } else {
        infrastructureFailure = false;
        break;
      }
    }

    if (infrastructureFailure) {
      throw new RuntimeException(
          "Infrastructure failure on test matrix after 3x retry " + testMatrixId);
    }

    System.out.println("Successfully scheduled test matrix: " + testMatrixId);

    if (testMatrixId == null) {
      throw new IllegalStateException("test matrix is null");
    }

    testMatrixIds.add(testMatrixId);
  }

  @Override
  public Object call() throws Exception {
    run();
    return null;
  }
}
