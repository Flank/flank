package ftl.gc

import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper
import ftl.args.AndroidArgs
import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.config.FtlConstants.GCS_PREFIX
import ftl.util.Utils.fatalError
import ftl.util.Utils.join
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths

object GcStorage {

    val storageOptions: StorageOptions by lazy {
        val builder = StorageOptions.newBuilder()
        if (FtlConstants.useMock) builder.setHost(FtlConstants.localhost)
        builder.build()
    }

    val storage: Storage by lazy {
        if (FtlConstants.useMock) {
            LocalStorageHelper.getOptions().service
        } else {
            storageOptions.service
        }
    }

    private fun upload(file: String, rootGcsBucket: String, runGcsPath: String): String =
        upload(
            file = file,
            fileBytes = Files.readAllBytes(Paths.get(file)),
            rootGcsBucket = rootGcsBucket,
            runGcsPath = runGcsPath
        )

    fun uploadAppApk(config: AndroidArgs, gcsBucket: String, runGcsPath: String): String =
        upload(config.appApk, gcsBucket, runGcsPath)

    fun uploadTestApk(config: AndroidArgs, gcsBucket: String, runGcsPath: String): String =
        upload(config.testApk, gcsBucket, runGcsPath)

    fun uploadXCTestZip(config: IosArgs, runGcsPath: String): String =
        upload(config.xctestrunZip, config.resultsBucket, runGcsPath)

    fun uploadXCTestFile(config: IosArgs, gcsBucket: String, runGcsPath: String, fileBytes: ByteArray): String =
        upload(
            file = config.xctestrunFile,
            fileBytes = fileBytes,
            rootGcsBucket = gcsBucket,
            runGcsPath = runGcsPath
        )

    fun downloadTestApk(config: AndroidArgs): String =
        download(config.testApk)

    private fun upload(file: String, fileBytes: ByteArray, rootGcsBucket: String, runGcsPath: String): String {
        val fileName = Paths.get(file).fileName.toString()
        val gcsFilePath = GCS_PREFIX + join(rootGcsBucket, runGcsPath, fileName)

        // 404 Not Found error when rootGcsBucket does not exist
        val fileBlob = BlobInfo.newBuilder(rootGcsBucket, join(runGcsPath, fileName)).build()

        try {
            storage.create(fileBlob, fileBytes)
        } catch (e: Exception) {
            fatalError(e)
        }

        return gcsFilePath
    }

    private fun download(gcsUriString: String): String {
        val gcsURI = URI.create(gcsUriString)
        val bucket = gcsURI.authority
        val path = gcsURI.path.drop(1) // Drop leading slash

        val outputFile = File.createTempFile("apk", null)
        outputFile.deleteOnExit()

        try {
            val blob = storage.get(bucket, path)
            val readChannel = blob.reader()
            val output = FileOutputStream(outputFile)
            output.channel.transferFrom(readChannel, 0, Long.MAX_VALUE)
            output.close()
        } catch (e: Exception) {
            fatalError(e)
        }

        return outputFile.path
    }
}
