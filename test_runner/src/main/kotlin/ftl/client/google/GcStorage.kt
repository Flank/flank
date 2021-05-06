package ftl.client.google

import com.google.api.client.http.GoogleApiLogger
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.Storage.BlobListOption.pageSize
import com.google.cloud.storage.Storage.BlobListOption.prefix
import com.google.cloud.storage.StorageOptions
import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper
import com.google.common.annotations.VisibleForTesting
import flank.common.join
import ftl.adapter.google.credential
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.config.FtlConstants.GCS_PREFIX
import ftl.config.FtlConstants.GCS_STORAGE_LINK
import ftl.run.exception.FlankGeneralError
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
            TestStorageProvider.storage
        } else {
            storageOptions.service
        }
    }

    @VisibleForTesting
    internal fun upload(file: String, rootGcsBucket: String, runGcsPath: String): String {
        if (file.startsWith(GCS_PREFIX)) return file
        return upload(
            filePath = file,
            fileBytes = Files.readAllBytes(Paths.get(file)),
            rootGcsBucket = rootGcsBucket,
            runGcsPath = runGcsPath
        )
    }

    fun uploadJunitXml(testResultXml: String, args: IArgs) {
        if (args.smartFlankGcsPath.isBlank() || args.smartFlankDisableUpload) return

        // bucket/path/to/object
        val rawPath = args.smartFlankGcsPath.drop(GCS_PREFIX.length)

        testResultXml.toByteArray().uploadWithProgress(
            bucket = rawPath.substringBefore('/'),
            path = rawPath.substringAfter('/'),
            name = "smart flank XML"
        )
    }

    private val duplicatedGcsPathCounter = ConcurrentHashMap<String, Int>()

    @VisibleForTesting
    internal fun upload(
        filePath: String,
        fileBytes: ByteArray,
        rootGcsBucket: String,
        runGcsPath: String
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
            fileBytes.uploadWithProgress(
                bucket = rootGcsBucket,
                path = validGcsPath,
                name = file.name
            )
            GCS_PREFIX + join(rootGcsBucket, validGcsPath)
        }
    }

    private fun ByteArray.uploadWithProgress(
        bucket: String,
        path: String,
        name: String
    ) {
        runWithProgress(
            startMessage = "Uploading [$name] to ${GCS_STORAGE_LINK + join(bucket, path).replace(name, "")}..",
            action = { storage.create(BlobInfo.newBuilder(bucket, path).build(), this) },
            onError = { throw FlankGeneralError("Error on uploading $name\nCause: $it") }
        )
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

    @VisibleForTesting
    internal fun exist(
        rootGcsBucket: String,
        runGcsPath: String
    ) = storage.list(rootGcsBucket, pageSize(1), prefix("$runGcsPath/")).values.count() > 0

    fun exist(gcsUriString: String) = with(URI.create(gcsUriString)) {
        storage.get(authority, path.dropLeadingSlash())?.exists() ?: false
    }

    private fun String.dropLeadingSlash() = drop(1)
}

internal fun gcStorageUpload(
    filePath: String,
    fileBytes: ByteArray,
    rootGcsBucket: String,
    runGcsPath: String
) = if (filePath.startsWith(GCS_PREFIX)) filePath else GcStorage.upload(filePath, fileBytes, rootGcsBucket, runGcsPath)

internal fun gcStorageExist(rootGcsBucket: String, runGcsPath: String) = GcStorage.exist(rootGcsBucket, runGcsPath)

internal fun gcStorageDownload(gcsUriString: String, ignoreError: Boolean = false) =
    GcStorage.download(gcsUriString, ignoreError)

object TestStorageProvider {
    init {
        require(FtlConstants.useMock) { "Storage provider can be used only during tests" }
    }

    private var backingStorage: Storage? = null

    val storage: Storage
        get() = backingStorage ?: LocalStorageHelper.getOptions().service.apply {
            backingStorage = this
        }

    fun clearStorage() {
        backingStorage = null
    }
}
