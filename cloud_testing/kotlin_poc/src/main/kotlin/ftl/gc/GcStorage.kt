package ftl.gc

import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import ftl.config.FtlConstants
import ftl.config.FtlConstants.GCS_PREFIX
import ftl.config.YamlConfig
import ftl.util.Utils.fatalError
import ftl.util.Utils.join
import java.nio.file.Files
import java.nio.file.Paths

object GcStorage {

    val storageOptions: StorageOptions by lazy {
        val builder = StorageOptions.newBuilder()
        if (FtlConstants.useMock) builder.setHost(FtlConstants.localhost)
        builder.build()
    }

    val storage: Storage by lazy {
        storageOptions.service
    }

    private fun upload(file: String, rootGcsBucket: String, runGcsPath: String): String {
        return upload(file = file,
                fileBytes = Files.readAllBytes(Paths.get(file)),
                rootGcsBucket = rootGcsBucket,
                runGcsPath = runGcsPath)
    }

    private fun upload(file: String, fileBytes: ByteArray, rootGcsBucket: String, runGcsPath: String): String {
        val fileName = Paths.get(file).fileName.toString()
        val gcsFilePath = GCS_PREFIX + join(rootGcsBucket, runGcsPath, fileName)

        if (FtlConstants.useMock) return gcsFilePath

        // 404 Not Found error when rootGcsBucket does not exist
        val fileBlob = BlobInfo.newBuilder(rootGcsBucket, join(runGcsPath, fileName)).build()

        try {
            storage.create(fileBlob, fileBytes)
        } catch (e: Exception) {
            fatalError(e)
        }

        return gcsFilePath
    }

    fun uploadAppApk(config: YamlConfig, gcsBucket: String, runGcsPath: String): String {
        return upload(config.appApk, gcsBucket, runGcsPath)
    }

    fun uploadTestApk(config: YamlConfig, gcsBucket: String, runGcsPath: String): String {
        return upload(config.testApk, gcsBucket, runGcsPath)
    }

    fun uploadXCTestZip(config: YamlConfig, runGcsPath: String): String {
        return upload(config.xctestrunZip, config.getGcsBucket(), runGcsPath)
    }

    fun uploadXCTestFile(config: YamlConfig, gcsBucket: String, runGcsPath: String, fileBytes: ByteArray): String {
        return upload(file = config.xctestrunFile,
                fileBytes = fileBytes,
                rootGcsBucket = gcsBucket,
                runGcsPath = runGcsPath)
    }
}
