import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.StorageOptions

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import GlobalConfig.bucketGcsPath
import Constants.GCS_PREFIX
import Utils.fatalError

object GcStorage {

    private val storage = StorageOptions.newBuilder().build().service

    fun uploadApk(apk: Path): String {
        val apkFileName = apk.fileName.toString()
        val gcsApkPath = GCS_PREFIX + Paths.get(bucketGcsPath, apkFileName).toString()

        // todo: check if bucketGcsPath exists. create if not.
        // 404 Not Found error when bucketGcsPath does not exist
        val apkBlob = BlobInfo.newBuilder(bucketGcsPath, apkFileName).build()

        try {
            storage.create(apkBlob, Files.readAllBytes(apk))
        } catch (e: Exception) {
            fatalError(e)
        }

        return gcsApkPath
    }
}
