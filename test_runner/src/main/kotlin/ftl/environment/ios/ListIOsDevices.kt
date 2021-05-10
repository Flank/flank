package ftl.environment.ios

import ftl.api.DeviceModel
import ftl.environment.MODEL_ID
import ftl.environment.MODEL_NAME
import ftl.environment.OS_VERSION_IDS
import ftl.environment.RESOLUTION
import ftl.environment.TAGS
import ftl.environment.TestEnvironmentInfo
import ftl.environment.createTableColumnFor
import ftl.environment.getOrCreateList
import ftl.environment.isValid
import ftl.environment.tagToSystemOutColorMapper
import ftl.util.applyColorsUsing
import ftl.util.buildTable

fun DeviceModel.Ios.Available.toCliTable() = list.toCliTable()

fun List<DeviceModel.Ios>.toCliTable() = createTestEnvironmentInfo().createIoDevicesTable()

fun List<DeviceModel.Ios>.createTestEnvironmentInfo() =
    fold(mutableMapOf<String, MutableList<String>>()) { deviceInfo, iOsDevice ->
        deviceInfo.apply {
            getOrCreateList(MODEL_ID).add(iOsDevice.id)
            getOrCreateList(MODEL_NAME).add(iOsDevice.name)
            getOrCreateList(RESOLUTION).add(iOsDevice.resolution)
            getOrCreateList(OS_VERSION_IDS).add(iOsDevice.supportedVersionIds.joinToString())
            getOrCreateList(TAGS).add(iOsDevice.tags.joinToString())
        }
    }

private val DeviceModel.Ios.resolution
    get() = if (screenX.isValid() && screenY.isValid()) "$screenY x $screenX" else "UNKNOWN"

private fun TestEnvironmentInfo.createIoDevicesTable() = buildTable(
    createTableColumnFor(MODEL_ID),
    createTableColumnFor(MODEL_NAME),
    createTableColumnFor(RESOLUTION),
    createTableColumnFor(OS_VERSION_IDS),
    createTableColumnFor(TAGS).applyColorsUsing(tagToSystemOutColorMapper)
)
