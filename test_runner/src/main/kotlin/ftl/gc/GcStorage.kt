package ftl.gc

import com.google.api.client.http.GoogleApiLogger
import com.google.api.services.toolresults.model.PerfMetricsSummary
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.Storage.BlobListOption.pageSize
import com.google.cloud.storage.Storage.BlobListOption.prefix
import com.google.cloud.storage.StorageOptions
import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper
import com.google.common.annotations.VisibleForTesting
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.config.FtlConstants.GCS_PREFIX
import ftl.config.credential
import ftl.json.MatrixMap
import ftl.log.logLn
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.parseAllSuitesXml
import ftl.reports.xml.xmlToString
import ftl.run.common.getMatrixFilePath
import ftl.run.exception.FlankGeneralError
import ftl.util.join
import ftl.util.runWithProgress
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
        builder.setCredentials(credential)

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

    fun upload(file: String, rootGcsBucket: String, runGcsPath: String): String {
        if (file.startsWith(GCS_PREFIX)) return file

        return upload(
            filePath = file,
            fileBytes = Files.readAllBytes(Paths.get(file)),
            rootGcsBucket = rootGcsBucket,
            runGcsPath = runGcsPath
        )
    }

    fun uploadJunitXml(testResult: JUnitTestResult, args: IArgs) {
        if (args.smartFlankGcsPath.isBlank() || args.smartFlankDisableUpload) return

        // bucket/path/to/object
        val rawPath = args.smartFlankGcsPath.drop(GCS_PREFIX.length)
        val bucket = rawPath.substringBefore('/')
        val name = rawPath.substringAfter('/')

        val fileBlob = BlobInfo.newBuilder(bucket, name).build()
        runWithProgress(
            startMessage = "Uploading smart flank XML",
            action = { storage.create(fileBlob, testResult.xmlToString().toByteArray()) },
            onError = { throw FlankGeneralError(it) }
        )
    }

    fun uploadPerformanceMetrics(perfMetricsSummary: PerfMetricsSummary, resultsBucket: String, resultDir: String) =
        runCatching {
            upload(
                "performanceMetrics.json",
                perfMetricsSummary.toPrettyString().toByteArray(),
                resultsBucket,
                resultDir
            )
        }.onFailure {
            logLn("Cannot upload performance metrics ${it.message}")
        }.getOrNull()

    fun uploadMatricesId(args: IArgs, matrixMap: MatrixMap) {
        if (args.disableResultsUpload) return
        upload(
            file = args.getMatrixFilePath(matrixMap).toString(),
            rootGcsBucket = args.resultsBucket,
            runGcsPath = args.resultsDir
        )
    }

    fun uploadReportResult(testResult: String, args: IArgs, fileName: String) {
        if (args.resultsBucket.isBlank() || args.resultsDir.isBlank() || args.disableResultsUpload) return
        upload(
            filePath = fileName,
            fileBytes = testResult.toByteArray(),
            rootGcsBucket = args.resultsBucket,
            runGcsPath = args.resultsDir
        )
    }

    fun uploadXCTestZip(args: IosArgs, runGcsPath: String): String =
        upload(args.xctestrunZip, args.resultsBucket, runGcsPath)

    fun uploadXCTestFile(fileName: String, gcsBucket: String, runGcsPath: String, fileBytes: ByteArray): String =
        upload(
            filePath = fileName,
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

    private val duplicatedGcsPathCounter = ConcurrentHashMap<String, Int>()

    @VisibleForTesting
    internal fun upload(
        filePath: String,
        fileBytes: ByteArray,
        rootGcsBucket: String,
        runGcsPath: String,
        storage: Storage = GcStorage.storage
    ): String {
        val file = File(filePath)
        return uploadCache.computeIfAbsent("$runGcsPath-${file.absolutePath}") {
            val gcsPath = join(runGcsPath, file.name)
            val index = duplicatedGcsPathCounter.merge(gcsPath, 0) { old, _ -> old + 1 }
            val validGcsPath = when {
                index == 0 -> gcsPath
                file.extension.isBlank() -> "${gcsPath}_$index"
                else -> gcsPath.replace(".${file.extension}", "_$index.${file.extension}")
            }

            // 404 Not Found error when rootGcsBucket does not exist
            val fileBlob = BlobInfo.newBuilder(rootGcsBucket, validGcsPath).build()
            runWithProgress(
                startMessage = "Uploading ${file.name}",
                action = { storage.create(fileBlob, fileBytes) },
                onError = { throw FlankGeneralError("Error on uploading ${file.name}\nCause: $it") }
            )
            GCS_PREFIX + join(rootGcsBucket, validGcsPath)
        }
    }

    fun download(gcsUriString: String, ignoreError: Boolean = false): String {
        val gcsURI = URI.create(gcsUriString)
        val bucket = gcsURI.authority
        val path = gcsURI.path.dropLeadingSlash()
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
                throw FlankGeneralError("Cannot download $gcsUriString", e)
            }
            outputFile.path
        }
    }

    fun exist(
        rootGcsBucket: String,
        runGcsPath: String
    ) = storage.list(rootGcsBucket, pageSize(1), prefix("$runGcsPath/")).values.count() > 0

    fun exist(gcsUriString: String) = with(URI.create(gcsUriString)) {
        storage.get(authority, path.dropLeadingSlash())?.exists() ?: false
    }

    private fun String.dropLeadingSlash() = drop(1)
}
