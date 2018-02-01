package ftl.gc

import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import ftl.config.FtlConstants.GCS_PREFIX
import ftl.config.YamlConfig
import ftl.util.Utils.fatalError
import ftl.util.Utils.join
import java.nio.file.Files
import java.nio.file.Paths

object GcStorage {

    val storage: Storage by lazy { StorageOptions.newBuilder().build().service }

    private fun uploadApk(apk: String, rootGcsBucket: String, runGcsPath: String): String {
        val apkFileName = Paths.get(apk).fileName.toString()
        val gcsApkPath = GCS_PREFIX + join(rootGcsBucket, runGcsPath, apkFileName)

        // 404 Not Found error when rootGcsBucket does not exist
        val apkBlob = BlobInfo.newBuilder(rootGcsBucket, join(runGcsPath, apkFileName)).build()

        try {
            storage.create(apkBlob, Files.readAllBytes(Paths.get(apk)))
        } catch (e: Exception) {
            fatalError(e)
        }

        return gcsApkPath
    }

    fun uploadAppApk(config: YamlConfig, runGcsPath: String): String {
        return uploadApk(config.appApk, config.rootGcsBucket, runGcsPath)
    }

    fun uploadTestApk(config: YamlConfig, runGcsPath: String): String {
        return uploadApk(config.testApk, config.rootGcsBucket, runGcsPath)
    }
}
