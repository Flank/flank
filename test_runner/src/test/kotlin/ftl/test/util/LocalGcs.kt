package ftl.test.util

import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper
import ftl.client.google.GcStorage
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

    fun uploadFileForTest(path: String) = uploadToMockGcs(Paths.get(path))

    fun clear() {
        LocalStorageHelper.getOptions()
    }
}
