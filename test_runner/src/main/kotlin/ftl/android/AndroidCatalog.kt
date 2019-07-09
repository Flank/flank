package ftl.android

import com.google.api.services.testing.model.AndroidDevice
import com.google.api.services.testing.model.AndroidDeviceCatalog
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

    private fun androidDeviceCatalog(projectId: String): AndroidDeviceCatalog {
        val cached = catalogMap[projectId]
        if (cached != null) return cached

        val newCatalog = GcTesting.get.testEnvironmentCatalog()
            .get("android")
            .setProjectId(projectId)
            .executeWithRetry().androidDeviceCatalog
        catalogMap[projectId] = newCatalog
        return newCatalog
    }

    fun androidModelIds(projectId: String): List<String> {
        val cached = modelMap[projectId]
        if (cached != null) return cached

        val newModels = androidDeviceCatalog(projectId).models.map { it.id }
        modelMap[projectId] = newModels
        return newModels
    }

    fun androidVersionIds(projectId: String): List<String> {
        val cached = versionMap[projectId]
        if (cached != null) return cached

        val newVersions = androidDeviceCatalog(projectId).versions.map { it.id }
        versionMap[projectId] = newVersions
        return newVersions
    }

    fun supportedDeviceConfig(modelId: String, versionId: String, projectId: String): DeviceConfigCheck {
        val foundModel = androidDeviceCatalog(projectId).models.find { it.id == modelId } ?: return UnsupportedModelId
        if (!androidVersionIds(projectId).contains(versionId)) return UnsupportedVersionId

        val supportedVersionIds = foundModel.supportedVersionIds
        supportedVersionIds?.let {
            if (!it.contains(versionId)) return IncompatibleModelVersion
        } ?: return UnsupportedModelId

        return SupportedDeviceConfig
    }

    fun isVirtualDevice(device: AndroidDevice?, projectId: String): Boolean {
        val modelId = device?.androidModelId ?: return false
        val form = androidDeviceCatalog(projectId).models.find { it.id == modelId }?.form ?: "PHYSICAL"
        return form == "VIRTUAL"
    }
}

sealed class DeviceConfigCheck
object SupportedDeviceConfig : DeviceConfigCheck()
object UnsupportedModelId : DeviceConfigCheck()
object UnsupportedVersionId : DeviceConfigCheck()
object IncompatibleModelVersion : DeviceConfigCheck()
