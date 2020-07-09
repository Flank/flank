package ftl.environment

import com.google.api.services.testing.model.AndroidDeviceCatalog
import ftl.util.SystemOutColor
import ftl.util.applyColorsUsing
import ftl.util.buildTable

fun AndroidDeviceCatalog.asPrintableTable() =
    models
        .fold(mutableMapOf<String, MutableList<String>>()) { devicesInfo, androidDevice ->
            devicesInfo.apply {
                getOrCreateList(MODEL_ID).add(androidDevice.codename)
                getOrCreateList(MAKE).add(androidDevice.manufacturer)
                getOrCreateList(MODEL_NAME).add(androidDevice.name)
                getOrCreateList(FORM).add(androidDevice.form)
                getOrCreateList(RESOLUTION).add("${androidDevice.screenY} x ${androidDevice.screenX}")
                getOrCreateList(OS_VERSION_IDS).add(androidDevice.supportedVersionIds?.joinToString().orEmpty())
                getOrCreateList(TAGS).add(androidDevice.tags?.joinToString().orEmpty())
            }
        }
        .createAndroidDevicesTable()

private fun DevicesInfo.createAndroidDevicesTable() = buildTable(
    createTableColumnFor(MODEL_ID),
    createTableColumnFor(MAKE),
    createTableColumnFor(MODEL_NAME),
    createTableColumnFor(FORM).applyColorsUsing(formToSystemOutColorMapper),
    createTableColumnFor(RESOLUTION),
    createTableColumnFor(OS_VERSION_IDS),
    createTableColumnFor(TAGS).applyColorsUsing(tagToSystemOutColorMapper)
)

private val formToSystemOutColorMapper: (String) -> SystemOutColor = {
    if (it == PHYSICAL_DEVICE) SystemOutColor.YELLOW else SystemOutColor.BLUE
}
