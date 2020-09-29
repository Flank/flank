package ftl.android

import com.google.api.services.testing.model.AndroidDevice
import com.google.api.services.testing.model.AndroidDeviceCatalog
import ftl.environment.android.asPrintableTable
import ftl.environment.android.getDescription
import ftl.environment.asPrintableTable
import ftl.environment.common.asPrintableTable
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

    fun devicesCatalogAsTable(projectId: String) = getModels(projectId).asPrintableTable()

    fun describeModel(projectId: String, modelId: String) = getModels(projectId).getDescription(modelId)

    private fun getModels(projectId: String) = deviceCatalog(projectId).models

    fun supportedVersionsAsTable(projectId: String) = getVersionsList(projectId).asPrintableTable()

    fun describeSoftwareVersion(projectId: String, versionId: String) = getVersionsList(projectId).getDescription(versionId)

    private fun getVersionsList(projectId: String) = deviceCatalog(projectId).versions

    fun supportedOrientationsAsTable(projectId: String) = deviceCatalog(projectId).runtimeConfiguration.orientations.asPrintableTable()

    fun localesAsTable(projectId: String) = getLocales(projectId).asPrintableTable()

    fun getLocaleDescription(projectId: String, locale: String) = getLocales(projectId).getLocaleDescription(locale)

    private fun getLocales(projectId: String) = deviceCatalog(projectId).runtimeConfiguration.locales

    fun androidModelIds(projectId: String) =
        modelMap.getOrPut(projectId) { deviceCatalog(projectId).models.map { it.id } }

    fun androidVersionIds(projectId: String) =
        versionMap.getOrPut(projectId) { deviceCatalog(projectId).versions.map { it.id } }

    fun supportedDeviceConfig(modelId: String, versionId: String, projectId: String): DeviceConfigCheck {
        val foundModel = deviceCatalog(projectId).models.find { it.id == modelId } ?: return UnsupportedModelId
        if (!androidVersionIds(projectId).contains(versionId)) return UnsupportedVersionId

        foundModel.supportedVersionIds?.let {
            if (!it.contains(versionId)) return IncompatibleModelVersion
        } ?: return UnsupportedModelId

        return SupportedDeviceConfig
    }

    fun isVirtualDevice(device: AndroidDevice?, projectId: String): Boolean = device
        ?.androidModelId
        ?.let { isVirtualDevice(it, projectId) }
        ?: false

    fun isVirtualDevice(modelId: String, projectId: String): Boolean {
        val form = deviceCatalog(projectId).models
            .find { it.id.equals(modelId, ignoreCase = true) }?.form
            ?: DeviceType.PHYSICAL.name.also {
                println("Unable to find device type for $modelId. PHYSICAL used as fallback in cost calculations")
            }

        return form.equals(DeviceType.VIRTUAL.name, ignoreCase = true)
    }
}

enum class DeviceType {
    VIRTUAL, PHYSICAL
}

sealed class DeviceConfigCheck
object SupportedDeviceConfig : DeviceConfigCheck()
object UnsupportedModelId : DeviceConfigCheck()
object UnsupportedVersionId : DeviceConfigCheck()
object IncompatibleModelVersion : DeviceConfigCheck()
