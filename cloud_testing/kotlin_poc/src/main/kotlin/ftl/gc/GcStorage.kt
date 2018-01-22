package ftl.gc

import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.StorageOptions
import ftl.config.FtlConstants.GCS_PREFIX
import ftl.config.FtlConstants.rootGcsBucket
import ftl.util.Utils.fatalError
import ftl.util.Utils.join
import java.nio.file.Files
import java.nio.file.Path

object GcStorage {

    val storage by lazy { StorageOptions.newBuilder().build().service }

    fun uploadApk(apk: Path, runGcsPath: String): String {
        val apkFileName = apk.fileName.toString()
        val gcsApkPath = GCS_PREFIX + join(rootGcsBucket, runGcsPath, apkFileName)

        // 404 Not Found error when rootGcsBucket does not exist
        val apkBlob = BlobInfo.newBuilder(rootGcsBucket, join(runGcsPath, apkFileName)).build()

        try {
            storage.create(apkBlob, Files.readAllBytes(apk))
        } catch (e: Exception) {
            fatalError(e)
        }

        return gcsApkPath
    }
}
