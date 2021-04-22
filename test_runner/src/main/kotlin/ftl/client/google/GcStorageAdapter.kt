package ftl.client.google

import ftl.config.FtlConstants

internal fun gcStorageUpload(
    filePath: String,
    fileBytes: ByteArray,
    rootGcsBucket: String,
    runGcsPath: String
) = if (filePath.startsWith(FtlConstants.GCS_PREFIX)) filePath
else GcStorage.upload(filePath, fileBytes, rootGcsBucket, runGcsPath)

internal fun gcStorageExist(rootGcsBucket: String, runGcsPath: String) = GcStorage.exist(rootGcsBucket, runGcsPath)
