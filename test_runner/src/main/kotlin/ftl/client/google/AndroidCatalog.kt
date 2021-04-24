package ftl.client.google

import com.google.testing.model.AndroidDevice
import com.google.testing.model.AndroidDeviceCatalog
import com.google.testing.model.AndroidModel
import com.google.testing.model.Orientation
import flank.common.logLn
import ftl.api.fetchAndroidOsVersion
import ftl.environment.android.getDescription
import ftl.environment.android.toCliTable
import ftl.environment.asPrintableTable
import ftl.environment.getLocaleDescription
import ftl.gc.GcTesting
import ftl.http.executeWithRetry

/**
 * Contains lists of possible Android device and version ids, as well as checks
 * to validate device configuration
 */
object AndroidCatalog {
    private val catalogMap: MutableMap<String, AndroidDeviceCatalog> = mutableMapOf()
    private val modelMap: MutableMap<String, List<String>> = mutableMapOf()
    private val versionMap: MutableMap<String, List<String>> = mutableMapOf()

    private fun deviceCatalog(projectId: String) = catalogMap.getOrPut(projectId) {
        GcTesting.get.testEnvironmentCatalog()
            .get("android")
            .setProjectId(projectId)
            .executeWithRetry()
            .androidDeviceCatalog
    }

    fun getModels(projectId: String): List<AndroidModel> = deviceCatalog(projectId).models.orEmpty()

    fun supportedVersionsAsTable(projectId: String) = fetchAndroidOsVersion(projectId).toCliTable()

    fun describeSoftwareVersion(projectId: String, versionId: String) = fetchAndroidOsVersion(projectId).getDescription(versionId)

    private fun getVersionsList(projectId: String) = deviceCatalog(projectId).versions

    fun supportedOrientations(projectId: String): List<Orientation> = deviceCatalog(projectId).runtimeConfiguration.orientations

    private fun localesAsTable(projectId: String) = getLocales(projectId).asPrintableTable()

    private fun getLocaleDescription(projectId: String, locale: String) = getLocales(projectId).getLocaleDescription(locale)

    internal fun getLocales(projectId: String) = deviceCatalog(projectId).runtimeConfiguration.locales

    fun androidModelIds(projectId: String) =
        modelMap.getOrPut(projectId) { deviceCatalog(projectId).models.map { it.id } }

    fun androidVersionIds(projectId: String) =
        versionMap.getOrPut(projectId) { deviceCatalog(projectId).versions.map { it.id } }

    fun isVirtualDevice(device: AndroidDevice?, projectId: String): Boolean = device
        ?.androidModelId
        ?.let { isVirtualDevice(it, projectId) }
        ?: false

    fun isVirtualDevice(modelId: String, projectId: String): Boolean {
        val form = deviceCatalog(projectId).models
            .find { it.id.equals(modelId, ignoreCase = true) }?.form
            ?: DeviceType.PHYSICAL.name.also {
                logLn("Unable to find device type for $modelId. PHYSICAL used as fallback in cost calculations")
            }

        return form.equals(DeviceType.VIRTUAL.name, ignoreCase = true) || form.equals(DeviceType.EMULATOR.name, ignoreCase = true)
    }
}

enum class DeviceType {
    VIRTUAL, PHYSICAL, EMULATOR
}

sealed class DeviceConfigCheck
object SupportedDeviceConfig : DeviceConfigCheck()
object UnsupportedModelId : DeviceConfigCheck()
object UnsupportedVersionId : DeviceConfigCheck()
object IncompatibleModelVersion : DeviceConfigCheck()
