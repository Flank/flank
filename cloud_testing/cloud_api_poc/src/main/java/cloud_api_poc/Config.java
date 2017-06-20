package cloud_api_poc;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.cloud.ServiceOptions;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Config {
  private Config() {}

  public static final String bucketGcsPath = "tmp_bucket_2";

  // gcloud config get-value project
  //
  // cat ~/.config/gcloud/active_config
  // cat ~/.config/gcloud/configurations/config_default
  //
  // export GOOGLE_APPLICATION_CREDENTIALS="path/to/secrets.json"
  private static void checkProjectId() {
    String projectId = ServiceOptions.getDefaultProjectId();

    if (projectId == null) {
      throw new RuntimeException(
          "Project ID not found. Is GOOGLE_APPLICATION_CREDENTIALS defined?\n"
              + " See https://github.com/GoogleCloudPlatform/google-cloud-java#specifying-a-project-id");
    }
  }

  static {
    checkProjectId();
  }

  private static Path getApk(String apkName) {
    String apkRoot = "../../test_app/apks";
    Path targetApk = Paths.get(apkRoot, apkName);

    if (!targetApk.toFile().exists()) {
      throw new RuntimeException(apkName + " doesn't exist!");
    }
    return targetApk;
  }

  public static Path getTestApk() {
    return getApk("app-debug-androidTest.apk");
  }

  public static Path getAppApk() {
    return getApk("app-debug.apk");
  }

  public static Credential getCredential() {
    Credential credential = null;
    try {
      credential = GoogleCredential.getApplicationDefault();
    } catch (Exception e) {
      Utils.fatalError(e);
    }

    return credential;
  }
}
