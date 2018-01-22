package ftl.config

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.cloud.ServiceOptions
import java.nio.file.Path
import java.nio.file.Paths

object FtlConstants {

    const val matrixIdsFile = "matrix_ids.json"
    const val applicationName = "Flank"
    val projectId = ServiceOptions.getDefaultProjectId()!!
    const val GCS_PREFIX = "gs://"
    val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()

    val httpTransport: NetHttpTransport by lazy {
        try {
            return@lazy GoogleNetHttpTransport.newTrustedTransport()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    val credential: Credential by lazy {
        try {
            return@lazy GoogleCredential.getApplicationDefault()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun getApk(apkName: String): Path {
        val apkRoot = "../../test_app/apks"
        val targetApk = Paths.get(apkRoot, apkName)

        if (!targetApk.toFile().exists()) {
            throw RuntimeException(apkName + " doesn't exist!")
        }
        return targetApk
    }

    val testApk = getApk("app-debug-androidTest.apk")
    val testErrorApk = getApk("error-androidTest.apk")

    val appApk = getApk("app-debug.apk")

    const val localResultsDir = "results"
    const val rootGcsBucket = "tmp_bucket_2"
}
