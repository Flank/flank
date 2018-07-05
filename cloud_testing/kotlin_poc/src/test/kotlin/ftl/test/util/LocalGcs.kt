package ftl.test.util

import com.google.cloud.storage.BlobInfo
import ftl.gc.GcStorage
import org.junit.Assert
import java.io.FileInputStream

object LocalGcs {
    private const val APP_APK = "app-debug.apk"
    private const val TEST_APK = "app-debug-androidTest.apk"

    private const val APP_APK_PATH = "../../test_app/apks/app-debug.apk"
    private const val TEST_APK_PATH = "../../test_app/apks/app-debug-androidTest.apk"

    const val TEST_BUCKET = "tmp_bucket_2"

    private const val APP_BLOB_PATH = APP_APK
    private const val TEST_BLOB_PATH = TEST_APK

    fun setupApks() {
        val testApkInputStream = FileInputStream(TEST_APK_PATH)
        val testApkBlobInfo = BlobInfo.newBuilder(TEST_BUCKET, TEST_BLOB_PATH).build()
        GcStorage.storage.create(testApkBlobInfo, testApkInputStream)

        Assert.assertTrue(
                GcStorage.storage.list(TEST_BUCKET).values
                        .map { it.name }
                        .any { TEST_BLOB_PATH == it }
        )

        val appApkInputStream = FileInputStream(APP_APK_PATH)
        val appApkBlobInfo = BlobInfo.newBuilder(TEST_BUCKET, APP_BLOB_PATH).build()
        GcStorage.storage.create(appApkBlobInfo, appApkInputStream)

        Assert.assertTrue(
                GcStorage.storage.list(TEST_BUCKET).values
                        .map { it.name }
                        .any { APP_BLOB_PATH == it }
        )
    }
}
