package ftl.android

import ftl.gc.GcTesting


/**
 * Contains lists of possible Android device and version ids, as well as checks
 * to validate device configuration
 */
object AndroidCatalog {
    private val androidDeviceCatalog by lazy {
        GcTesting.get.testEnvironmentCatalog().get("android").execute().androidDeviceCatalog
    }

    val androidModelIds by lazy { androidDeviceCatalog.models.map { it.id } }
    val androidVersionIds by lazy { androidDeviceCatalog.versions.map { it.id } }


    fun supportedDeviceConfig(modelId: String, versionId: String): DeviceConfigCheck {
        if (!androidModelIds.contains(modelId)) return UnsupportedModelId
        if (!androidVersionIds.contains(versionId)) return UnsupportedVersionId

        val supportedVersionIds = androidDeviceCatalog.models.find { it.id == modelId }?.supportedVersionIds
        supportedVersionIds?.let {
            if (!it.contains(versionId)) return IncompatibleModelVersion
        } ?: return UnsupportedModelId

        return SupportedDeviceConfig
    }
}

sealed class DeviceConfigCheck
object SupportedDeviceConfig: DeviceConfigCheck()
object UnsupportedModelId : DeviceConfigCheck()
object UnsupportedVersionId : DeviceConfigCheck()
object IncompatibleModelVersion : DeviceConfigCheck()