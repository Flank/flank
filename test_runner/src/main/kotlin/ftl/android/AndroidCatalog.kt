package ftl.android

import com.google.api.services.testing.model.AndroidDevice
import ftl.config.executeWithRetry
import ftl.gc.GcTesting

/**
 * Contains lists of possible Android device and version ids, as well as checks
 * to validate device configuration
 */
object AndroidCatalog {
    private val androidDeviceCatalog by lazy {
        GcTesting.get.testEnvironmentCatalog().get("android").executeWithRetry().androidDeviceCatalog
    }

    val androidModelIds by lazy { androidDeviceCatalog.models.map { it.id } }
    val androidVersionIds by lazy { androidDeviceCatalog.versions.map { it.id } }

    fun supportedDeviceConfig(modelId: String, versionId: String): DeviceConfigCheck {
        val foundModel = androidDeviceCatalog.models.find { it.id == modelId } ?: return UnsupportedModelId
        if (!androidVersionIds.contains(versionId)) return UnsupportedVersionId

        val supportedVersionIds = foundModel.supportedVersionIds
        supportedVersionIds?.let {
            if (!it.contains(versionId)) return IncompatibleModelVersion
        } ?: return UnsupportedModelId

        return SupportedDeviceConfig
    }

    fun isVirtualDevice(device: AndroidDevice?): Boolean {
        val modelId = device?.androidModelId ?: return false
        val form = androidDeviceCatalog.models.find { it.id == modelId }?.form ?: "PHYSICAL"
        return form == "VIRTUAL"
    }
}

sealed class DeviceConfigCheck
object SupportedDeviceConfig : DeviceConfigCheck()
object UnsupportedModelId : DeviceConfigCheck()
object UnsupportedVersionId : DeviceConfigCheck()
object IncompatibleModelVersion : DeviceConfigCheck()
