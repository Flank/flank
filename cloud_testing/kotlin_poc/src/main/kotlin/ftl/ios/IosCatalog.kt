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
    private val iosModelIds by lazy {
        iosDeviceCatalog.models
                .filter { it.supportedVersionIds?.isNotEmpty() ?: false }
                .map { it.id }
    }
    private val iosVersionIds by lazy { iosDeviceCatalog.versions.map { it.id } }

    fun model(modelId: String): String {
        if (!iosModelIds.contains(modelId)) {
            throw RuntimeException("Invalid iOS modelId '$modelId'.\nValid models are: $iosModelIds")
        }
        return modelId
    }

    fun version(versionId: String): String {
        if (!iosVersionIds.contains(versionId)) {
            throw RuntimeException("Invalid iOS versionId '$versionId'.\nValid versions are: $iosVersionIds")
        }
        return versionId
    }

    fun supported(modelId: String, versionId: String): Boolean {
        val model = iosDeviceCatalog.models.find { it.id == modelId }
        return model?.supportedVersionIds?.contains(versionId) ?: false
    }
}
