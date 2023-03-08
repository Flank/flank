package ftl.client.google

import com.google.api.client.http.HttpHeaders
import com.google.testing.model.IosDeviceCatalog
import com.google.testing.model.IosModel
import com.google.testing.model.Orientation
import ftl.config.Device
import ftl.config.FtlConstants.GCS_PROJECT_HEADER
import ftl.environment.getLocaleDescription
import ftl.environment.ios.getDescription
import ftl.environment.ios.iosVersionsToCliTable
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
                .setRequestHeaders(HttpHeaders().set(GCS_PROJECT_HEADER, projectId))
                .executeWithRetry()
                .iosDeviceCatalog
                .filterDevicesWithoutSupportedVersions()
        }
    } catch (e: java.lang.Exception) {
        throw java.lang.RuntimeException(e)
    }

    private fun IosDeviceCatalog.filterDevicesWithoutSupportedVersions() =
        setModels(models.filterNotNull().filter { it.supportedVersionIds?.isNotEmpty() ?: false })
}
