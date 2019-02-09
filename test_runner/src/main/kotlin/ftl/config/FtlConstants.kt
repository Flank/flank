package ftl.config

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.googleapis.util.Utils
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Date

object FtlConstants {
    var useMock = false
    val macOS = System.getProperty("os.name") == "Mac OS X"
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

    val credential by lazy {
        val cred = if (useMock) {
            GoogleCredentials.newBuilder().setAccessToken(AccessToken("ss", Date(9999))).build()
        } else {
            GoogleCredentials.getApplicationDefault()
        }

        // https://github.com/googleapis/google-auth-library-java
        HttpCredentialsAdapter(cred)
    }

    const val localResultsDir = "results"

    fun configFileName(args: IArgs): String {
        return when (args) {
            is IosArgs -> defaultIosConfig
            is AndroidArgs -> defaultAndroidConfig
            else -> throw RuntimeException("Unknown config type")
        }
    }
}
