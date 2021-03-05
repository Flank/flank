package flank.scripts.ops.firebase

import flank.common.defaultCredentialPath
import flank.common.downloadFile
import java.io.File

fun saveServiceAccount(serviceAccount: String) =
    when {
        serviceAccount.startsWith("http", true) -> downloadFile(serviceAccount, defaultCredentialPath.toAbsolutePath())
        serviceAccount.endsWith(".json", true) -> saveFromFile(serviceAccount)
        else -> saveFromStr(serviceAccount)
    }

private fun saveFromFile(path: String) {
    File(path).copyTo(defaultCredentialPath.toFile(), overwrite = true)
}

private fun saveFromStr(serviceAccountData: String) {
    defaultCredentialPath.toFile().writeText(serviceAccountData)
}
