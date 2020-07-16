package ftl.environment

import com.google.api.services.testing.model.AndroidModel
import ftl.util.SystemOutColor
import ftl.util.applyColorsUsing
import ftl.util.buildTable

fun List<AndroidModel>.asPrintableTable() = createTestEnvironmentInfo().createAndroidDevicesTable()

private fun List<AndroidModel>.createTestEnvironmentInfo() =
    fold(mutableMapOf<String, MutableList<String>>()) { devicesInfo, androidDevice ->
        devicesInfo.apply {
            getOrCreateList(MODEL_ID).add(androidDevice.codename.orUnknown())
            getOrCreateList(MAKE).add(androidDevice.manufacturer.orUnknown())
            getOrCreateList(MODEL_NAME).add(androidDevice.name.orUnknown())
            getOrCreateList(FORM).add(androidDevice.form.orUnknown())
            getOrCreateList(RESOLUTION).add(androidDevice.resolution)
            getOrCreateList(OS_VERSION_IDS).add(androidDevice.supportedVersionIds?.joinToString().orEmpty())
            getOrCreateList(TAGS).add(androidDevice.tags?.joinToString().orEmpty())
        }
    }

private fun TestEnvironmentInfo.createAndroidDevicesTable() = buildTable(
    createTableColumnFor(MODEL_ID),
    createTableColumnFor(MAKE),
    createTableColumnFor(MODEL_NAME),
    createTableColumnFor(FORM).applyColorsUsing(formToSystemOutColorMapper),
    createTableColumnFor(RESOLUTION),
    createTableColumnFor(OS_VERSION_IDS),
    createTableColumnFor(TAGS).applyColorsUsing(tagToSystemOutColorMapper)
)

private val AndroidModel.resolution
    get() = if (screenX == null || screenY == null) "UNKNOWN" else "$screenY x $screenX"

private val formToSystemOutColorMapper: (String) -> SystemOutColor = {
    if (it == PHYSICAL_DEVICE) SystemOutColor.YELLOW else SystemOutColor.BLUE
}
