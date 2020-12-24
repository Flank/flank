package ftl.config

import com.bugsnag.Bugsnag
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.googleapis.util.Utils
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.FlankGeneralError
import ftl.util.BugsnagInitHelper.initBugsnag
import ftl.util.readRevision

object FtlConstants {
    var useMock = false

    const val localhost = "http://localhost:8080"
    const val defaultLocale = "en"
    const val defaultOrientation = "portrait"
    const val defaultIosModel = "iphone8"
    const val defaultIosVersion = "13.6"
    const val defaultAndroidModel = "NexusLowRes"
    const val defaultAndroidVersion = "28"
    const val defaultIosConfig = "./flank.ios.yml"
    const val defaultAndroidConfig = "./flank.yml"
    const val indent = "  "
    const val matrixIdsFile = "matrix_ids.json"
    const val applicationName = "Flank"
    const val GCS_PREFIX = "gs://"
    const val runTimeout = "-1"

    val JSON_FACTORY: JsonFactory by lazy { Utils.getDefaultJsonFactory() }

    val bugsnag: Bugsnag? by lazy {
        initBugsnag(useMock)?.apply { setAppVersion(readRevision()) }
    }

    val httpTransport: NetHttpTransport by lazy {
        try {
            return@lazy GoogleNetHttpTransport.newTrustedTransport()
        } catch (e: Exception) {
            throw FlankGeneralError(e)
        }
    }

    fun configFileName(args: IArgs): String {
        return when (args) {
            is IosArgs -> defaultIosConfig
            is AndroidArgs -> defaultAndroidConfig
            else -> throw FlankConfigurationError("Unknown config type")
        }
    }
}
