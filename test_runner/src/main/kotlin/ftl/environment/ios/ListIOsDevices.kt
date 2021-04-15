package ftl.environment.ios

import com.google.testing.model.IosModel
import ftl.adapter.environment.MODEL_ID
import ftl.adapter.environment.MODEL_NAME
import ftl.adapter.environment.OS_VERSION_IDS
import ftl.adapter.environment.RESOLUTION
import ftl.adapter.environment.TAGS
import ftl.adapter.environment.TestEnvironmentInfo
import ftl.adapter.environment.createTableColumnFor
import ftl.adapter.environment.getOrCreateList
import ftl.adapter.environment.orUnknown
import ftl.adapter.environment.tagToSystemOutColorMapper
import ftl.util.applyColorsUsing
import ftl.util.buildTable

fun List<IosModel>.asPrintableTable() = createTestEnvironmentInfo().createIoDevicesTable()

fun List<IosModel>.createTestEnvironmentInfo() =
    fold(mutableMapOf<String, MutableList<String>>()) { deviceInfo, iOsDevice ->
        deviceInfo.apply {
            getOrCreateList(MODEL_ID).add(iOsDevice.id.orUnknown())
            getOrCreateList(MODEL_NAME).add(iOsDevice.name.orUnknown())
            getOrCreateList(RESOLUTION).add(iOsDevice.resolution)
            getOrCreateList(OS_VERSION_IDS).add(iOsDevice.supportedVersionIds?.joinToString().orEmpty())
            getOrCreateList(TAGS).add(iOsDevice.tags?.joinToString().orEmpty())
        }
    }

private val IosModel.resolution
    get() = if (screenX == null || screenY == null) "UNKNOWN" else "$screenY x $screenX"

private fun TestEnvironmentInfo.createIoDevicesTable() = buildTable(
    createTableColumnFor(MODEL_ID),
    createTableColumnFor(MODEL_NAME),
    createTableColumnFor(RESOLUTION),
    createTableColumnFor(OS_VERSION_IDS),
    createTableColumnFor(TAGS).applyColorsUsing(tagToSystemOutColorMapper)
)
