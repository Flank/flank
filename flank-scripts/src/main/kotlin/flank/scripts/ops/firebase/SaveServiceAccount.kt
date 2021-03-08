package flank.scripts.ops.firebase

import flank.common.defaultCredentialPath
import flank.common.downloadFile
import java.io.File
import java.nio.file.Path

fun saveServiceAccount(
    serviceAccount: String,
    serviceAccountPath: Path = defaultCredentialPath.toAbsolutePath()
) = when {
    serviceAccount.startsWith("http", true) -> downloadFile(serviceAccount, serviceAccountPath)
    serviceAccount.endsWith(".json", true) -> saveFromFile(serviceAccount, serviceAccountPath)
    else -> saveFromStr(serviceAccount, serviceAccountPath)
}

private fun saveFromFile(
    path: String,
    serviceAccountPath: Path = defaultCredentialPath.toAbsolutePath()
) {
    File(path).copyTo(serviceAccountPath.toFile(), overwrite = true)
}

private fun saveFromStr(
    serviceAccountData: String,
    serviceAccountPath: Path = defaultCredentialPath.toAbsolutePath()
) {
    serviceAccountPath.toFile().writeText(serviceAccountData)
}
