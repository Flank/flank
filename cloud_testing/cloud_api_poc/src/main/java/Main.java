import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static Path getApk(String apkName) {
        String apkRoot = "../../test_app/apks";
        Path targetApk = Paths.get(apkRoot + "/app-debug-androidTest.apk");

        if (!targetApk.toFile().exists()) {
            throw new RuntimeException(apkName + " doesn't exist!");
        }
        return targetApk;
    }

    private static Path getTestApk() {
        return getApk("/app-debug-androidTest.apk");
    }

    private static Path getAppApk() {
        return getApk("/app-debug.apk");
    }

    // gcloud config get-value project
    //
    // cat ~/.config/gcloud/active_config
    // cat ~/.config/gcloud/configurations/config_default
    //
    // echo $GCLOUD_PROJECT
    private static String getProjectId() {
        String projectId = System.getenv("GCLOUD_PROJECT");
        if (projectId != null && !projectId.isEmpty()) {
            return projectId;
        }

        throw new RuntimeException("GCLOUD_PROJECT environment variable not found");
    }

    private static Storage getStorageService() {
        StorageOptions.Builder storageOptionsBuilder = StorageOptions.newBuilder();
        storageOptionsBuilder.setProjectId(getProjectId());
        return storageOptionsBuilder.build().getService();
    }

    private static void fatalError(Exception e) {
        e.printStackTrace();
        System.exit(-1);
    }

    public static void main(String[] args) {
        // todo: upload apk via google-cloud-storage
        Path testApk = getTestApk();
        Path appApk = getAppApk();

        Storage storage = getStorageService();

        // 404 Not Found error when bucket does not exist
        String bucket = "tmp_bucket_2";
        BlobInfo testApkBlob = BlobInfo.newBuilder(bucket, testApk.getFileName().toString()).build();

        try {
            byte[] bytes = Files.readAllBytes(testApk);
            // create the blob in one request.
            storage.create(testApkBlob, bytes);
        } catch (Exception e) {
            fatalError(e);
        }

        // todo: schedule test via google-api-services-testing

        // todo: fetch test results via google-api-services-toolresults
    }
}
