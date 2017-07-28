package cloud_api_poc;

import static cloud_api_poc.Utils.fatalError;

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

  private void run() {
    TestMatrix testMatrix = null;
    try {
      testMatrix = testMatrixCreate.execute();
    } catch (Exception e) {
      try {
        testMatrix = testMatrixCreate.execute();
      } catch (Exception e2) {
        fatalError(e2);
      }
    }

    if (testMatrix == null) {
      throw new IllegalStateException("test matrix is null");
    }

    testMatrixIds.add(testMatrix.getTestMatrixId());
  }

  @Override
  public Object call() throws Exception {
    run();
    return null;
  }
}
