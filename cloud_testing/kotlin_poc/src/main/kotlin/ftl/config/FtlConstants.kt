package ftl.config

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.googleapis.testing.auth.oauth2.MockGoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory

object FtlConstants {
    var useMock = false
    const val localhost = "http://localhost:8080"

    const val indent = "  "
    const val matrixIdsFile = "matrix_ids.json"
    const val applicationName = "Flank"
    const val GCS_PREFIX = "gs://"
    val JSON_FACTORY: JacksonFactory by lazy { JacksonFactory.getDefaultInstance() }

    val httpTransport: NetHttpTransport by lazy {
        try {
            return@lazy GoogleNetHttpTransport.newTrustedTransport()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    val credential: Credential by lazy {
        // TODO: only CredTmp.authorize if there's no service credential

        try {
            if (useMock) {
                return@lazy MockGoogleCredential.Builder()
                        .setTransport(MockGoogleCredential.newMockHttpTransportWithSampleTokenResponse())
                        .build()
            }

            val defaultCredential = GoogleCredential.getApplicationDefault()
            // Scope is required.
            // https://developers.google.com/identity/protocols/googlescopes
            // https://developers.google.com/identity/protocols/application-default-credentials
            // https://cloud.google.com/sdk/gcloud/reference/alpha/compute/instances/set-scopes
            return@lazy defaultCredential.createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        CredTmp.authorize()
    }

    const val localResultsDir = "results"
}
