package ftl.gc

import com.google.api.client.auth.oauth2.StoredCredential
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import java.io.File

// https://github.com/googleapis/google-oauth-java-client
// GoogleAuthorizationCodeFlow usage based on https://developers.google.com/sheets/api/quickstart/java
object GcAuth {
    private val HOME = System.getProperty("user.home")
    private val CRED_FOLDER = File(HOME, ".flank/")
    val CRED = File(CRED_FOLDER, "StoredCredential")

    private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
    private var HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    private var DATA_STORE_FACTORY = FileDataStoreFactory(CRED_FOLDER)

    // https://github.com/bootstraponline/gcloud_cli/blob/40521a6e297830b9f652a9ab4d8002e309b4353a/google-cloud-sdk/platform/gsutil/gslib/utils/system_util.py#L177
    private const val CLIENT_ID = "32555940559.apps.googleusercontent.com"
    private const val CLIENT_SECRET = "ZmssLNjJy2998hD4CTg2ejr2"

    private val flow by lazy {
        // https://github.com/bootstraponline/gcloud_cli/blob/e4b5e01610abad2e31d8a6edb20b17b2f84c5395/google-cloud-sdk/lib/googlecloudsdk/core/config.py#L167
        val scopes = listOf("https://www.googleapis.com/auth/cloud-platform")

        GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, scopes)
            .setDataStoreFactory(DATA_STORE_FACTORY)
            .setAccessType("offline")
            .build()
    }
    private const val DATA_STORE_KEY = "default"

    fun hasUserAuth(): Boolean {
        return defaultCredential() != null
    }

    private fun createGoogleCredential(accessToken: String): GoogleCredential {
        return GoogleCredential.Builder()
            .setTransport(HTTP_TRANSPORT)
            .setJsonFactory(JSON_FACTORY)
            .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
            .build()
            .setAccessToken(accessToken)
    }

    private fun StoredCredential.toGoogleCredential(): GoogleCredential {
        return createGoogleCredential(this.accessToken)
    }

    private fun defaultCredential(): GoogleCredential? {
        return flow.credentialDataStore[DATA_STORE_KEY]?.toGoogleCredential()
    }
}
