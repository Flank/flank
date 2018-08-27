package ftl.ios

import ftl.gc.GcTesting

/**
 * Validates iOS device model and version
 *
 * note:  500 Internal Server Error is returned on invalid model id/version
 **/
object IosCatalog {
    private val iosDeviceCatalog by lazy {
        try {
            GcTesting.get.testEnvironmentCatalog().get("ios").execute().iosDeviceCatalog
        } catch (e: java.lang.Exception) {
            throw java.lang.RuntimeException("""
Unable to access the test environment catalog. Firebase Test Lab for iOS is currently in beta.
Request access for your project via the following form:
  https://goo.gl/forms/wAxbiNEP2pxeIRG82
If this project has already been granted access, please email ftl-ios-feedback@google.com for support.""", e)
        }
    }

    fun supported(modelId: String, versionId: String): Boolean {
        val model = iosDeviceCatalog.models.find { it.id == modelId }
        return model?.supportedVersionIds?.contains(versionId) ?: false
    }
}
