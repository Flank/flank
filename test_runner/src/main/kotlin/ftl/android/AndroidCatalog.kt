package ftl.android

import com.google.api.services.testing.model.AndroidDevice
import com.google.api.services.testing.model.AndroidDeviceCatalog
import ftl.environment.android.asPrintableTable
import ftl.environment.common.asPrintableTable
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

    fun devicesCatalogAsTable(projectId: String) = deviceCatalog(projectId).models.asPrintableTable()

    fun supportedVersionsAsTable(projectId: String) = deviceCatalog(projectId).versions.asPrintableTable()

    fun supportedOrientationsAsTable(projectId: String) = deviceCatalog(projectId).runtimeConfiguration.orientations.asPrintableTable()
    
    fun localesAsTable(projectId: String) = deviceCatalog(projectId).runtimeConfiguration.locales.asPrintableTable()

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

    fun isVirtualDevice(device: AndroidDevice?, projectId: String): Boolean {
        val modelId = device?.androidModelId ?: return false
        val form = deviceCatalog(projectId).models.find { it.id == modelId }?.form ?: "PHYSICAL"
        return form == "VIRTUAL"
    }
}

sealed class DeviceConfigCheck
object SupportedDeviceConfig : DeviceConfigCheck()
object UnsupportedModelId : DeviceConfigCheck()
object UnsupportedVersionId : DeviceConfigCheck()
object IncompatibleModelVersion : DeviceConfigCheck()
