package ftl

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.cloud.ServiceOptions

import java.nio.file.Path
import java.nio.file.Paths

object GlobalConfig {

    var bucketGcsPath = "tmp_bucket_2"
    var downloadXml = true

    // gcloud config get-value project
    //
    // cat ~/.config/gcloud/active_config
    // cat ~/.config/gcloud/configurations/config_default
    //
    // export GOOGLE_APPLICATION_CREDENTIALS="path/to/secrets.json"
    private fun checkProjectId() {
        ServiceOptions.getDefaultProjectId() ?: throw RuntimeException(
                "Project ID not found. Is GOOGLE_APPLICATION_CREDENTIALS defined?\n" + " See https://github.com/GoogleCloudPlatform/google-cloud-java#specifying-a-project-id")

    }

    init {
        checkProjectId()
    }

    private fun getApk(apkName: String): Path {
        val apkRoot = "../../test_app/apks"
        val targetApk = Paths.get(apkRoot, apkName)

        if (!targetApk.toFile().exists()) {
            throw RuntimeException(apkName + " doesn't exist!")
        }
        return targetApk
    }

    val testApk: Path
        get() = getApk("app-debug-androidTest.apk")

    val appApk: Path
        get() = getApk("app-debug.apk")

    val credential: Credential?
        get() {
            var credential: Credential? = null
            try {
                credential = GoogleCredential.getApplicationDefault()
            } catch (e: Exception) {
                Utils.fatalError(e)
            }

            return credential
        }
}
