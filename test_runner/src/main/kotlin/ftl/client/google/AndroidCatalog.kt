package ftl.client.google

import com.google.testing.model.AndroidDevice
import com.google.testing.model.AndroidDeviceCatalog
import com.google.testing.model.AndroidModel
import com.google.testing.model.Orientation
import ftl.http.executeWithRetry
import ftl.presentation.cli.firebase.test.reportmanager.ReportManagerState
import ftl.presentation.publish

/**
 * Contains lists of possible Android device and version ids, as well as checks
 * to validate device configuration
 */
object AndroidCatalog {
    private val catalogMap: MutableMap<String, AndroidDeviceCatalog> = mutableMapOf()
    private val modelMap: MutableMap<String, List<String>> = mutableMapOf()
    private val versionMap: MutableMap<String, List<String>> = mutableMapOf()

    private fun androidDeviceCatalog(projectId: String) = catalogMap.getOrPut(projectId) {
        GcTesting.get.testEnvironmentCatalog()
            .get("android")
            .setProjectId(projectId)
            .executeWithRetry()
            .androidDeviceCatalog
            .filterDevicesWithoutSupportedVersions()
    }

    private fun AndroidDeviceCatalog.filterDevicesWithoutSupportedVersions() =
        setModels(models.filterNotNull().filter { it.supportedVersionIds?.isNotEmpty() ?: false })

    fun getModels(projectId: String): List<AndroidModel> = androidDeviceCatalog(projectId).models.orEmpty()

    fun supportedOrientations(projectId: String): List<Orientation> =
        androidDeviceCatalog(projectId).runtimeConfiguration.orientations

    internal fun getLocales(projectId: String) = androidDeviceCatalog(projectId).runtimeConfiguration.locales

    fun androidModelIds(projectId: String) =
        modelMap.getOrPut(projectId) { androidDeviceCatalog(projectId).models.map { it.id } }

    fun androidVersionIds(projectId: String) =
        versionMap.getOrPut(projectId) { androidDeviceCatalog(projectId).versions.map { it.id } }

    fun isVirtualDevice(device: AndroidDevice?, projectId: String): Boolean = device
        ?.androidModelId
        ?.let { isVirtualDevice(it, projectId) }
        ?: false

    fun isVirtualDevice(modelId: String, projectId: String): Boolean {
        // iOS catalog raises errors due to FTL backend blocking access from non-white listed project ids
        // work around this by manually looking for iphone/ipad in the model id.
        val isIos = listOf("ipad", "iphone").any { modelId.contains(it, ignoreCase = true) }
        if (isIos) return false

        val form = androidDeviceCatalog(projectId).models
            .find { it.id.equals(modelId, ignoreCase = true) }?.form
            ?: DeviceType.PHYSICAL.name.also {
                ReportManagerState.Log(
                    "Unable to find device type for $modelId. PHYSICAL used as fallback in cost calculations"
                ).publish()
            }

        return form.equals(DeviceType.VIRTUAL.name, ignoreCase = true) || form.equals(
            DeviceType.EMULATOR.name,
            ignoreCase = true
        )
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
