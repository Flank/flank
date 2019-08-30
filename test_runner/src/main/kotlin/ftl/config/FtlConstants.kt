package ftl.config

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.bugsnag.Bugsnag
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.googleapis.util.Utils
import com.google.api.client.http.GoogleApiLogger
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.gc.UserAuth
import ftl.http.HttpTimeoutIncrease
import ftl.util.Utils.fatalError
import ftl.util.Utils.readRevision
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Date
import org.slf4j.LoggerFactory

object FtlConstants {
    var useMock = false

    private val osName = System.getProperty("os.name")?.toLowerCase() ?: ""

    val isMacOS: Boolean by lazy {
        val isMacOS = osName.indexOf("mac") >= 0
        println("isMacOS = $isMacOS ($osName)")
        isMacOS
    }

    val isWindows: Boolean by lazy {
       osName.indexOf("win") >= 0
    }

    const val localhost = "http://localhost:8080"

    const val defaultLocale = "en"
    const val defaultOrientation = "portrait"
    const val defaultIosModel = "iphone8"
    const val defaultIosVersion = "12.0"
    const val defaultAndroidModel = "NexusLowRes"
    const val defaultAndroidVersion = "28"
    const val defaultIosConfig = "./flank.ios.yml"
    const val defaultAndroidConfig = "./flank.yml"
    const val indent = "  "
    const val matrixIdsFile = "matrix_ids.json"
    const val applicationName = "Flank"
    const val GCS_PREFIX = "gs://"
    val JSON_FACTORY: JsonFactory by lazy { Utils.getDefaultJsonFactory() }

    val bugsnag = Bugsnag(if (useMock) null else "3d5f8ba4ee847d6bb51cb9c347eda74f")

    init {
        bugsnag.setAppVersion(readRevision())
        (LoggerFactory.getLogger(Bugsnag::class.java) as Logger).level = Level.OFF
    }

    val httpTransport: NetHttpTransport by lazy {
        try {
            return@lazy GoogleNetHttpTransport.newTrustedTransport()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    val defaultCredentialPath: Path by lazy {
        Paths.get(System.getProperty("user.home"), ".config/gcloud/application_default_credentials.json")
    }

    val credential: GoogleCredentials by lazy {
        when {
            useMock -> GoogleCredentials.create(AccessToken("mock", Date()))
            UserAuth.exists() -> UserAuth.load()
            else -> try {
                GoogleApiLogger.silenceComputeEngine()
                ServiceAccountCredentials.getApplicationDefault()
            } catch (e: IOException) {
                fatalError("Error: Failed to read service account credential.\n${e.message}")
                throw RuntimeException("never thrown")
            }
        }
    }

    val httpCredential: HttpRequestInitializer by lazy {
        if (useMock) {
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

    fun configFileName(args: IArgs): String {
        return when (args) {
            is IosArgs -> defaultIosConfig
            is AndroidArgs -> defaultAndroidConfig
            else -> throw RuntimeException("Unknown config type")
        }
    }
}
