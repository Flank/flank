package ftl.environment

import com.google.api.services.testing.model.IosModel
import ftl.util.applyColorsUsing
import ftl.util.buildTable

fun List<IosModel>.asPrintableTable() = createTestEnvironmentInfo().createIoDevicesTable()

fun List<IosModel>.createTestEnvironmentInfo() =
    fold(mutableMapOf<String, MutableList<String>>()) { deviceInfo, iOsDevice ->
        deviceInfo.apply {
            getOrCreateList(MODEL_ID).add(iOsDevice.id)
            getOrCreateList(MODEL_NAME).add(iOsDevice.name)
            getOrCreateList(RESOLUTION).add("${iOsDevice.screenY} x ${iOsDevice.screenX}")
            getOrCreateList(OS_VERSION_IDS).add(iOsDevice.supportedVersionIds?.joinToString().orEmpty())
            getOrCreateList(TAGS).add(iOsDevice.tags?.joinToString().orEmpty())
        }
    }

private fun TestEnvironmentInfo.createIoDevicesTable() = buildTable(
    createTableColumnFor(MODEL_ID),
    createTableColumnFor(MODEL_NAME),
    createTableColumnFor(RESOLUTION),
    createTableColumnFor(OS_VERSION_IDS),
    createTableColumnFor(TAGS).applyColorsUsing(tagToSystemOutColorMapper)
)
