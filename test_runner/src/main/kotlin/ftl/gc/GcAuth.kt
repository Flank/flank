package ftl.gc

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import ftl.config.FtlConstants
import java.io.File
import java.io.IOException

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

    fun hasUserAuth(): Boolean {
        return CRED.exists()
    }

    private fun Credential.toGoogleCredential(): GoogleCredential {
        return GoogleCredential.Builder()
            .setTransport(HTTP_TRANSPORT)
            .setJsonFactory(JSON_FACTORY)
            .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
            .build()
            .setAccessToken(this.accessToken)
    }

    @Throws(IOException::class)
    fun authorizeUser(): GoogleCredential {
        if (FtlConstants.useMock) return GoogleCredential()
        // https://github.com/bootstraponline/gcloud_cli/blob/e4b5e01610abad2e31d8a6edb20b17b2f84c5395/google-cloud-sdk/lib/googlecloudsdk/core/config.py#L167
        val scopes = listOf("https://www.googleapis.com/auth/cloud-platform")

        val flow = GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, scopes)
            .setDataStoreFactory(DATA_STORE_FACTORY)
            .setAccessType("offline")
            .build()

        val authCode = AuthorizationCodeInstalledApp(flow, LocalServerReceiver())
        val dataStoreKey = "default"

        return authCode.authorize(dataStoreKey).toGoogleCredential()
    }
}
