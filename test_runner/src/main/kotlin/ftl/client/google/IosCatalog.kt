package ftl.client.google

import com.google.testing.model.IosDeviceCatalog
import com.google.testing.model.IosModel
import com.google.testing.model.Orientation
import ftl.config.Device
import ftl.environment.getLocaleDescription
import ftl.environment.ios.getDescription
import ftl.environment.ios.iosVersionsToCliTable
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

    fun getModels(projectId: String): List<IosModel> = iosDeviceCatalog(projectId).models.orEmpty()

    fun softwareVersionsAsTable(projectId: String) = getVersionsList(projectId).iosVersionsToCliTable()

    fun describeSoftwareVersion(projectId: String, versionId: String) =
        getVersionsList(projectId).getDescription(versionId)

    private fun getVersionsList(projectId: String) = iosDeviceCatalog(projectId).versions

    private fun getLocaleDescription(projectId: String, locale: String) = getLocales(projectId).getLocaleDescription(locale)

    internal fun getLocales(projectId: String) = iosDeviceCatalog(projectId).runtimeConfiguration.locales

    fun supportedOrientations(projectId: String): List<Orientation> =
        iosDeviceCatalog(projectId).runtimeConfiguration.orientations

    fun supportedXcode(version: String, projectId: String) = xcodeVersions(projectId).contains(version)

    private fun xcodeVersions(projectId: String) =
        xcodeMap.getOrPut(projectId) { iosDeviceCatalog(projectId).xcodeVersions.map { it.version } }

    fun Device.getSupportedVersionId(projectId: String): List<String> = iosDeviceCatalog(projectId).models.find { it.id == model }?.supportedVersionIds
        ?: emptyList()

    // Device catalogMap is different depending on the project id
    private fun iosDeviceCatalog(
        projectId: String
    ) = try {
        catalogMap.getOrPut(projectId) {
            GcTesting.get.testEnvironmentCatalog()
                .get("ios")
                .setProjectId(projectId)
                .executeWithRetry()
                .iosDeviceCatalog
        }
    } catch (e: java.lang.Exception) {
        throw java.lang.RuntimeException(
            """
Unable to access the test environment catalogMap. Firebase Test Lab for iOS is currently in beta.
Request access for your project via the following form:
  https://goo.gl/forms/wAxbiNEP2pxeIRG82

If this project has already been granted access, please make sure you are using a project
on the Blaze or Flame billing plans, and that you have run
gcloud config set billing/quota_project project

If you are still having issues, please email ftl-ios-feedback@google.com for support.""",
            e
        )
    }
}
