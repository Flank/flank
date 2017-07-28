package cloud_api_poc;

import static cloud_api_poc.Config.bucketGcsPath;
import static cloud_api_poc.Constants.projectId;
import static cloud_api_poc.Utils.fatalError;

import com.google.common.collect.Lists;
import com.google.testing.Testing;
import com.google.testing.model.*;

public abstract class GcTestMatrix {
  private GcTestMatrix() {}

  public static Testing.Projects.TestMatrices.Create build(
      String appApkGcsPath,
      String testApkGcsPath,
      AndroidMatrix androidMatrix,
      String testTargets) {
    // https://github.com/bootstraponline/studio-google-cloud-testing/blob/203ed2890c27a8078cd1b8f7ae12cf77527f426b/firebase-testing/src/com/google/gct/testing/launcher/CloudTestsLauncher.java#L120
    TestMatrix testMatrix = new TestMatrix();
    testMatrix.setClientInfo(new ClientInfo().setName("Flank"));

    // https://cloud.google.com/sdk/gcloud/reference/beta/test/android/run
    String testTimeout = "600s"; // must be in seconds

    // Use default instrumentationTestRunner
    //   String instrumentationTestRunner = "";
    //   .setTestRunnerClass(instrumentationTestRunner)

    AndroidInstrumentationTest androidInstrumentation =
        new AndroidInstrumentationTest()
            .setAppApk(new FileReference().setGcsPath(appApkGcsPath))
            .setTestApk(new FileReference().setGcsPath(testApkGcsPath));

    if (testTargets != null) {
      androidInstrumentation.setTestTargets(Lists.newArrayList(testTargets));
    }

    testMatrix.setTestSpecification(
        new TestSpecification()
            .setTestTimeout(testTimeout)
            .setAndroidInstrumentationTest(androidInstrumentation));

    testMatrix.setResultStorage(
        new ResultStorage()
            .setGoogleCloudStorage(new GoogleCloudStorage().setGcsPath(bucketGcsPath)));
    testMatrix.setEnvironmentMatrix(new EnvironmentMatrix().setAndroidMatrix(androidMatrix));

    try {
      return GcTesting.get().projects().testMatrices().create(projectId, testMatrix);
    } catch (Exception e) {
      fatalError(e);
    }

    throw new RuntimeException("Failed to create test matrix");
  }

  public static void cancel(String testMatrixId) {
    try {
      GcTesting.get().projects().testMatrices().cancel(projectId, testMatrixId);
    } catch (Exception e) {
      try {
        GcTesting.get().projects().testMatrices().cancel(projectId, testMatrixId);
      } catch (Exception e2) {
        // :(
        e2.printStackTrace();
      }
    }
  }

  public static TestMatrix refresh(String testMatrixId) {
    try {
      return GcTesting.get().projects().testMatrices().get(projectId, testMatrixId).execute();
    } catch (Exception e) {
      fatalError(e);
    }
    throw new RuntimeException("Failed to refresh");
  }
}
