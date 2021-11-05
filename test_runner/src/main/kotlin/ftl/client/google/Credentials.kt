package ftl.client.google

import com.google.api.client.http.GoogleApiLogger
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.apache.v2.ApacheHttpTransport
import com.google.auth.http.HttpTransportFactory
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import flank.common.defaultCredentialPath
import flank.common.isWindows
import ftl.config.FtlConstants
import ftl.http.HttpTimeoutIncrease
import ftl.run.exception.FlankGeneralError
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.ProxyAuthenticationStrategy
import org.apache.http.impl.conn.DefaultProxyRoutePlanner
import java.io.IOException
import java.util.Date

val credential: GoogleCredentials by lazy {
    when {
        FtlConstants.useMock -> GoogleCredentials.create(AccessToken("mock", Date()))
        else ->
            runCatching {
                GoogleApiLogger.silenceComputeEngine()
                val httpTransportFactory = getHttpTransportFactory()
                if (httpTransportFactory != null) {
                    ServiceAccountCredentials.getApplicationDefault(httpTransportFactory)
                } else {
                    ServiceAccountCredentials.getApplicationDefault()
                }
            }.getOrElse {
                when {
                    isWindows -> loadCredentialsWindows()
                    else -> loadUserAuth(it.message.orEmpty())
                }
            }
    }
}

private fun getHttpTransportFactory(): HttpTransportFactory? {
    val proxyHost = System.getProperty("http.proxyHost") ?: return null
    val proxyPort = System.getProperty("http.proxyPort")?.toInt() ?: return null
    val proxyPassword = System.getProperty("http.proxyPassword") ?: return null
    val proxyUsername = System.getProperty("http.proxyUser") ?: return null

    val proxyHostDetails = HttpHost(proxyHost, proxyPort)
    val httpRoutePlanner = DefaultProxyRoutePlanner(proxyHostDetails)

    val credentialsProvider = BasicCredentialsProvider()
    credentialsProvider.setCredentials(
        AuthScope(proxyHostDetails.getHostName(), proxyHostDetails.getPort()),
        UsernamePasswordCredentials(proxyUsername, proxyPassword)
    )

    val httpClient = ApacheHttpTransport.newDefaultHttpClientBuilder()
        .setRoutePlanner(httpRoutePlanner)
        .setProxyAuthenticationStrategy(ProxyAuthenticationStrategy.INSTANCE)
        .setDefaultCredentialsProvider(credentialsProvider)
        .build()

    val httpTransport = ApacheHttpTransport(httpClient)
    return HttpTransportFactory { httpTransport }
}

private fun loadCredentialsWindows() = runCatching {
    loadGoogleAccountCredentials()
}.getOrElse {
    loadUserAuth(it.message.orEmpty())
}

private fun loadGoogleAccountCredentials(): GoogleCredentials = try {
    GoogleCredentials.fromStream(defaultCredentialPath.toFile().inputStream())
} catch (e: IOException) {
    throw FlankGeneralError("Error: Failed to read service account credential.\n${e.message}")
}

private fun loadUserAuth(topLevelErrorMessage: String) =
    if (UserAuth.exists()) UserAuth.load()
    else throw FlankGeneralError("Error: Failed to read service account credential.\n$topLevelErrorMessage")

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
