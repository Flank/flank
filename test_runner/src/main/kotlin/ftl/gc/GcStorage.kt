package ftl.gc

import com.google.api.client.http.GoogleApiLogger
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.config.FtlConstants.GCS_PREFIX
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.parseAllSuitesXml
import ftl.reports.xml.xmlToString
import ftl.util.ProgressBar
import ftl.util.join
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap

object GcStorage {

    private val uploadCache: ConcurrentHashMap<String, String> = ConcurrentHashMap()
    private val downloadCache: ConcurrentHashMap<String, String> = ConcurrentHashMap()

    val storageOptions: StorageOptions by lazy {
        val builder = StorageOptions.newBuilder()
        if (FtlConstants.useMock) builder.setHost(FtlConstants.localhost)
        builder.setCredentials(FtlConstants.credential)

        // The oauth lib for user auth needs to be replaced
        // https://github.com/Flank/flank/issues/464#issuecomment-455227703
        // builder.setCredentials(FtlConstants.googleCredentials)

        builder.build()
    }

    val storage: Storage by lazy {
        GoogleApiLogger.silenceComputeEngine()
        if (FtlConstants.useMock) {
            LocalStorageHelper.getOptions().service
        } else {
            storageOptions.service
        }
    }

    fun upload(file: String, rootGcsBucket: String, runGcsPath: String, counter: Int? = null): String {
        if (file.startsWith(GCS_PREFIX)) return file

        return upload(
            file = file,
            fileBytes = Files.readAllBytes(Paths.get(file)),
            rootGcsBucket = rootGcsBucket,
            runGcsPath = runGcsPath,
            counter = counter
        )
    }

    fun uploadJunitXml(testResult: JUnitTestResult, args: IArgs) {
        if (args.smartFlankGcsPath.isEmpty() || args.smartFlankDisableUpload) return

        // bucket/path/to/object
        val rawPath = args.smartFlankGcsPath.drop(GCS_PREFIX.length)
        val bucket = rawPath.substringBefore('/')
        val name = rawPath.substringAfter('/')

        val fileBlob = BlobInfo.newBuilder(bucket, name).build()

        val progress = ProgressBar()
        try {
            progress.start("Uploading smart flank XML")
            storage.create(fileBlob, testResult.xmlToString().toByteArray())
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            progress.stop()
        }
    }

    fun uploadCiJUnitXml(testResult: JUnitTestResult, args: IArgs, fileName: String) {
        if (args.resultsBucket.isBlank() || args.resultsDir.isBlank()) return
        upload(
            file = fileName,
            fileBytes = testResult.xmlToString().toByteArray(),
            rootGcsBucket = args.resultsBucket,
            runGcsPath = args.resultsDir
        )
    }

    fun uploadXCTestZip(args: IosArgs, runGcsPath: String): String =
        upload(args.xctestrunZip, args.resultsBucket, runGcsPath)

    fun uploadXCTestFile(fileName: String, gcsBucket: String, runGcsPath: String, fileBytes: ByteArray): String =
        upload(
            file = fileName,
            fileBytes = fileBytes,
            rootGcsBucket = gcsBucket,
            runGcsPath = runGcsPath
        )

    // junit xml may not exist. ignore error if it doesn't exist
    fun downloadJunitXml(args: IArgs): JUnitTestResult? {
        val oldXmlPath = download(args.smartFlankGcsPath, ignoreError = true)
        if (oldXmlPath.isNotEmpty()) {
            return parseAllSuitesXml(Paths.get(oldXmlPath))
        }

        return null
    }

    private fun upload(file: String, fileBytes: ByteArray, rootGcsBucket: String, runGcsPath: String, counter: Int? = null): String {
        val filePath = Paths.get(file)
        val fileName = generateApkFileName(filePath.fileName.toString(), counter)
        val absolutePath = filePath.toAbsolutePath().toString()
        return uploadCache[absolutePath] ?: uploadCache.computeIfAbsent(absolutePath) {
            val gcsFilePath = GCS_PREFIX + join(rootGcsBucket, runGcsPath, fileName)

            // 404 Not Found error when rootGcsBucket does not exist
            val fileBlob = BlobInfo.newBuilder(rootGcsBucket, join(runGcsPath, fileName)).build()

            val progress = ProgressBar()
            try {
                progress.start("Uploading $fileName")
                storage.create(fileBlob, fileBytes)
            } catch (e: Exception) {
                throw RuntimeException(e)
            } finally {
                progress.stop()
            }
            gcsFilePath
        }
    }

    fun download(gcsUriString: String, ignoreError: Boolean = false): String {
        val gcsURI = URI.create(gcsUriString)
        val bucket = gcsURI.authority
        val path = gcsURI.path.drop(1) // Drop leading slash
        return downloadCache[path] ?: downloadCache.computeIfAbsent(path) {
            val outputFile = File.createTempFile("tmp", null)
            outputFile.deleteOnExit()

            try {
                val blob = storage.get(bucket, path)
                blob.reader().use { readChannel ->
                    FileOutputStream(outputFile).use {
                        it.channel.transferFrom(readChannel, 0, Long.MAX_VALUE)
                    }
                }
            } catch (e: Exception) {
                if (ignoreError) return@computeIfAbsent ""
                throw RuntimeException("Cannot download $gcsUriString", e)
            }
            outputFile.path
        }
    }
}
