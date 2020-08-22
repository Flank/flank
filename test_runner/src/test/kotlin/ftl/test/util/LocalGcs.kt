package ftl.test.util

import com.google.cloud.storage.BlobInfo
import ftl.gc.GcStorage
import ftl.run.exception.FlankGeneralError
import org.junit.Assert
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object LocalGcs {

    private const val TEST_BUCKET = "tmp_bucket_2"

    private fun uploadToMockGcs(path: Path) {
        if (!path.toFile().exists()) {
            throw FlankGeneralError("File doesn't exist at path $path")
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
        val appApk = "../test_projects/android/apks/app-debug.apk"
        val testApk = "../test_projects/android/apks/app-debug-androidTest.apk"
        val ipaZip = "./src/test/kotlin/ftl/fixtures/tmp/earlgrey_example.zip"
        val xctestrun = "./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleSwiftTests_iphoneos13.4-arm64e.xctestrun"

        listOf(appApk, testApk, ipaZip, xctestrun).forEach { file ->
            uploadToMockGcs(Paths.get(file))
        }
    }
}
