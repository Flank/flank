package ftl.ios

import ftl.http.executeWithRetry
import ftl.gc.GcTesting

/**
 * Validates iOS device model and version
 *
 * note:  500 Internal Server Error is returned on invalid model id/version
 **/
object IosCatalog {
    private val xcodeVersions by lazy {
        iosDeviceCatalog.xcodeVersions.map { it.version }
    }

    private val iosDeviceCatalog by lazy {
        try {
            GcTesting.get.testEnvironmentCatalog().get("ios").executeWithRetry().iosDeviceCatalog
        } catch (e: java.lang.Exception) {
            throw java.lang.RuntimeException(
                """
Unable to access the test environment catalog. Firebase Test Lab for iOS is currently in beta.
Request access for your project via the following form:
  https://goo.gl/forms/wAxbiNEP2pxeIRG82

If this project has already been granted access, please make sure you are using a project
on the Blaze or Flame billing plans, and that you have run
gcloud config set billing/quota_project project

If you are still having issues, please email ftl-ios-feedback@google.com for support.""", e
            )
        }
    }

    fun supportedXcode(version: String): Boolean {
        return xcodeVersions.contains(version)
    }

    fun supportedDevice(modelId: String, versionId: String): Boolean {
        val model = iosDeviceCatalog.models.find { it.id == modelId }
        return model?.supportedVersionIds?.contains(versionId) ?: false
    }
}
