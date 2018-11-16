package ftl.gc

import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.config.FtlConstants.GCS_PREFIX
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.parseIosXml
import ftl.reports.xml.xmlToString
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

    fun uploadJunitXml(testResult: JUnitTestResult, args: IArgs) {
        if (args.junitGcsPath.isEmpty()) return

        // bucket/path/to/object
        val rawPath = args.junitGcsPath.drop(GCS_PREFIX.length)
        val bucket = rawPath.substringBefore('/')
        val name = rawPath.substringAfter('/')

        val fileBlob = BlobInfo.newBuilder(bucket, name).build()

        try {
            storage.create(fileBlob, testResult.xmlToString().toByteArray())
        } catch (e: Exception) {
            fatalError(e)
        }
    }

    fun uploadAppApk(args: AndroidArgs, gcsBucket: String, runGcsPath: String): String =
        upload(args.appApk, gcsBucket, runGcsPath)

    fun uploadTestApk(args: AndroidArgs, gcsBucket: String, runGcsPath: String): String =
        upload(args.testApk, gcsBucket, runGcsPath)

    fun uploadXCTestZip(args: IosArgs, runGcsPath: String): String =
        upload(args.xctestrunZip, args.resultsBucket, runGcsPath)

    fun uploadXCTestFile(args: IosArgs, gcsBucket: String, runGcsPath: String, fileBytes: ByteArray): String =
        upload(
            file = args.xctestrunFile,
            fileBytes = fileBytes,
            rootGcsBucket = gcsBucket,
            runGcsPath = runGcsPath
        )

    fun downloadTestApk(args: AndroidArgs): String =
        download(args.testApk)

    // junit xml may not exist. ignore error if it doesn't exist
    fun downloadJunitXml(args: IArgs): JUnitTestResult? {
        val oldXmlPath = download(args.junitGcsPath, ignoreError = true)
        if (oldXmlPath.isNotEmpty()) {
            return parseIosXml(Paths.get(oldXmlPath))
        }

        return null
    }

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

    private fun download(gcsUriString: String, ignoreError: Boolean = false): String {
        val gcsURI = URI.create(gcsUriString)
        val bucket = gcsURI.authority
        val path = gcsURI.path.drop(1) // Drop leading slash

        val outputFile = File.createTempFile("tmp", null)
        outputFile.deleteOnExit()

        try {
            val blob = storage.get(bucket, path)
            val readChannel = blob.reader()
            val output = FileOutputStream(outputFile)
            output.channel.transferFrom(readChannel, 0, Long.MAX_VALUE)
            output.close()
        } catch (e: Exception) {
            if (ignoreError) return ""
            fatalError(e)
        }

        return outputFile.path
    }
}
