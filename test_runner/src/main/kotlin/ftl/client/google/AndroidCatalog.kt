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

    private fun deviceCatalog(projectId: String) = catalogMap.getOrPut(projectId) {
        GcTesting.get.testEnvironmentCatalog()
            .get("android")
            .setProjectId(projectId)
            .executeWithRetry()
            .androidDeviceCatalog
            .filterDevicesWithoutSupportedVersions()
    }

    private fun AndroidDeviceCatalog.filterDevicesWithoutSupportedVersions() =
        setModels(models.filterNotNull().filter { it.supportedVersionIds?.isNotEmpty() ?: false })

    fun getModels(projectId: String): List<AndroidModel> = deviceCatalog(projectId).models.orEmpty()

    fun supportedOrientations(projectId: String): List<Orientation> =
        deviceCatalog(projectId).runtimeConfiguration.orientations

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
