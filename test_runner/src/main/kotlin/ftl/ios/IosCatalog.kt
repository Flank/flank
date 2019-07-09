package ftl.ios

import com.google.api.services.testing.model.IosDeviceCatalog
import ftl.gc.GcTesting
import ftl.http.executeWithRetry

/**
 * Validates iOS device model and version
 *
 * note:  500 Internal Server Error is returned on invalid model id/version
 **/
object IosCatalog {
    private val catalogMap: MutableMap<String, IosDeviceCatalog> = mutableMapOf()
    private val xcodeMap: MutableMap<String, List<String>> = mutableMapOf()

    fun xcodeVersions(projectId: String): List<String> {
        val cached = xcodeMap[projectId]
        if (cached != null) return cached

        val newVersions = iosDeviceCatalog(projectId).xcodeVersions.map { it.version }
        xcodeMap[projectId] = newVersions
        return newVersions
    }

    // Device catalogMap is different depending on the project id
    fun iosDeviceCatalog(projectId: String): IosDeviceCatalog {
        val cached = catalogMap[projectId]
        if (cached != null) return cached

        try {
            val newCatalog = GcTesting.get.testEnvironmentCatalog()
                .get("ios")
                .setProjectId(projectId)
                .executeWithRetry().iosDeviceCatalog
            catalogMap[projectId] = newCatalog
            return newCatalog
        } catch (e: java.lang.Exception) {
            throw java.lang.RuntimeException(
                """
Unable to access the test environment catalogMap. Firebase Test Lab for iOS is currently in beta.
Request access for your project via the following form:
  https://goo.gl/forms/wAxbiNEP2pxeIRG82

If this project has already been granted access, please make sure you are using a project
on the Blaze or Flame billing plans, and that you have run
gcloud config set billing/quota_project project

If you are still having issues, please email ftl-ios-feedback@google.com for support.""", e
            )
        }
    }

    fun supportedXcode(version: String, projectId: String): Boolean {
        return xcodeVersions(projectId).contains(version)
    }

    fun supportedDevice(modelId: String, versionId: String, projectId: String): Boolean {
        val model = iosDeviceCatalog(projectId).models.find { it.id == modelId }
        return model?.supportedVersionIds?.contains(versionId) ?: false
    }
}
