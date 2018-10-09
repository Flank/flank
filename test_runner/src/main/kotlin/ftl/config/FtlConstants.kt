package ftl.config

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.googleapis.testing.auth.oauth2.MockGoogleCredential
import com.google.api.client.googleapis.util.Utils
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import ftl.http.TimeoutHttpRequestInitializer

object FtlConstants {
    var useMock = false
    val macOS = System.getProperty("os.name") == "Mac OS X"
    const val localhost = "http://localhost:8080"

    const val defaultIosConfig = "./flank.ios.yml"
    const val defaultAndroidConfig = "./flank.yml"
    const val indent = "  "
    const val matrixIdsFile = "matrix_ids.json"
    const val applicationName = "Flank"
    const val GCS_PREFIX = "gs://"
    val JSON_FACTORY: JsonFactory by lazy { Utils.getDefaultJsonFactory() }

    val httpTransport: NetHttpTransport by lazy {
        try {
            return@lazy GoogleNetHttpTransport.newTrustedTransport()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    val credential: HttpRequestInitializer by lazy {
        try {
            // Scope is required.
            // https://developers.google.com/identity/protocols/googlescopes
            // https://developers.google.com/identity/protocols/application-default-credentials
            // https://cloud.google.com/sdk/gcloud/reference/alpha/compute/instances/set-scopes
            val credential = if (useMock) {
                MockGoogleCredential.Builder()
                    .setTransport(MockGoogleCredential.newMockHttpTransportWithSampleTokenResponse())
                    .build()
            } else {
                GoogleCredential.getApplicationDefault()
                    .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
            }

            return@lazy TimeoutHttpRequestInitializer(credential)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    const val localResultsDir = "results"
}
