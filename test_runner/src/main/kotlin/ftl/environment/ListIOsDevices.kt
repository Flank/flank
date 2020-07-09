package ftl.environment

import com.google.api.services.testing.model.IosDeviceCatalog
import ftl.util.applyColorsUsing
import ftl.util.buildTable

fun IosDeviceCatalog.asPrintableTable() =
    models
        .fold(mutableMapOf<String, MutableList<String>>()) { deviceInfo, iOsDevice ->
            deviceInfo.apply {
                getOrCreateList(MODEL_ID).add(iOsDevice.id)
                getOrCreateList(MODEL_NAME).add(iOsDevice.name)
                getOrCreateList(RESOLUTION).add("${iOsDevice.screenY} x ${iOsDevice.screenX}")
                getOrCreateList(OS_VERSION_IDS).add(iOsDevice.supportedVersionIds?.joinToString().orEmpty())
                getOrCreateList(TAGS).add(iOsDevice.tags?.joinToString().orEmpty())
            }
        }
        .createIoDevicesTable()

private fun DevicesInfo.createIoDevicesTable() = buildTable(
    createTableColumnFor(MODEL_ID),
    createTableColumnFor(MODEL_NAME),
    createTableColumnFor(RESOLUTION),
    createTableColumnFor(OS_VERSION_IDS),
    createTableColumnFor(TAGS).applyColorsUsing(tagToSystemOutColorMapper)
)
