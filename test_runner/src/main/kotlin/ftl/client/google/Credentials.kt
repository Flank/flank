package ftl.client.google

import com.google.api.client.http.GoogleApiLogger
import com.google.api.client.http.HttpRequestInitializer
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import flank.common.defaultCredentialPath
import flank.common.isWindows
import ftl.config.FtlConstants
import ftl.http.HttpTimeoutIncrease
import ftl.run.exception.FlankGeneralError
import java.io.IOException
import java.util.Date

val credential: GoogleCredentials by lazy {
    when {
        FtlConstants.useMock -> GoogleCredentials.create(AccessToken("mock", Date()))
        UserAuth.exists() -> UserAuth.load()
        else ->
            runCatching {
                GoogleApiLogger.silenceComputeEngine()
                ServiceAccountCredentials.getApplicationDefault()
            }.getOrElse {
                if (isWindows) loadGoogleAccountCredentials()
                else throw FlankGeneralError("Error: Failed to read service account credential.\n${it.message}")
            }
    }
}

private fun loadGoogleAccountCredentials(): GoogleCredentials = try {
    GoogleCredentials.fromStream(defaultCredentialPath.toFile().inputStream())
} catch (e: IOException) {
    throw FlankGeneralError("Error: Failed to read service account credential.\n${e.message}")
}

val httpCredential: HttpRequestInitializer by lazy {
    if (FtlConstants.useMock) {
        HttpRequestInitializer {}
    } else {
        // Authenticate with https://github.com/googleapis/google-auth-library-java
        // Scope is required.
        // https://developers.google.com/identity/protocols/googlescopes
        // https://developers.google.com/identity/protocols/application-default-credentials
        // https://cloud.google.com/sdk/gcloud/reference/alpha/compute/instances/set-scopes
        HttpTimeoutIncrease(credential.createScoped(listOf("https://www.googleapis.com/auth/cloud-platform")))
    }
}
