package ftl.test.util

import com.google.cloud.storage.BlobInfo
import ftl.gc.GcStorage
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import org.junit.Assert

object LocalGcs {

    private const val TEST_BUCKET = "tmp_bucket_2"

    private fun uploadToMockGcs(path: Path) {
        if (!path.toFile().exists()) {
            throw RuntimeException("File doesn't exist at path $path")
        }

        val fileName = path.fileName.toString()
        val testApkBytes = Files.readAllBytes(path)
        val testApkBlobInfo = BlobInfo.newBuilder(TEST_BUCKET, fileName).build()
        GcStorage.storage.create(testApkBlobInfo, testApkBytes)

        Assert.assertTrue(
            GcStorage.storage.list(TEST_BUCKET).values
                .map { it.name }
                .any { fileName == it }
        )
    }

    fun uploadFiles() {
        val appApk = "../test_app/apks/app-debug.apk"
        val testApk = "../test_app/apks/app-debug-androidTest.apk"
        val ipaZip = "./src/test/kotlin/ftl/fixtures/tmp/ios_earlgrey2.zip"
        val xctestrun = "./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleSwiftTests_iphoneos12.1-arm64e.xctestrun"

        listOf(appApk, testApk, ipaZip, xctestrun).forEach { file ->
            uploadToMockGcs(Paths.get(file))
        }
    }
}
