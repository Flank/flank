package ftl.adapter.google

import ftl.config.FtlConstants

internal fun GcStorage.uploadToApiModel(
    filePath: String,
    fileBytes: ByteArray,
    rootGcsBucket: String,
    runGcsPath: String
): String {
    return if (filePath.startsWith(FtlConstants.GCS_PREFIX)) filePath
    else upload(filePath, fileBytes, rootGcsBucket, runGcsPath)
}

internal fun GcStorage.existsToApiModel(
    rootGcsBucket: String,
    runGcsPath: String
): Boolean {
    return exist(rootGcsBucket, runGcsPath)
}
