package cloud_api_poc;

import static cloud_api_poc.Config.bucketGcsPath;
import static cloud_api_poc.Constants.GCS_PREFIX;
import static cloud_api_poc.Utils.fatalError;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class GcStorage {
  private GcStorage() {}

  private static final Storage storage = StorageOptions.newBuilder().build().getService();

  public static String uploadApk(Path apk) {
    String apkFileName = apk.getFileName().toString();
    String gcsApkPath = GCS_PREFIX + Paths.get(bucketGcsPath, apkFileName).toString();

    // todo: check if bucketGcsPath exists. create if not.
    // 404 Not Found error when bucketGcsPath does not exist
    BlobInfo apkBlob = BlobInfo.newBuilder(bucketGcsPath, apkFileName).build();

    try {
      storage.create(apkBlob, Files.readAllBytes(apk));
    } catch (Exception e) {
      fatalError(e);
    }

    return gcsApkPath;
  }
}
