package ftl.environment.android

import com.google.testing.model.AndroidModel
import ftl.environment.EMULATOR_DEVICE
import ftl.environment.FORM
import ftl.environment.MAKE
import ftl.environment.MODEL_ID
import ftl.environment.MODEL_NAME
import ftl.environment.OS_VERSION_IDS
import ftl.environment.PHYSICAL_DEVICE
import ftl.environment.RESOLUTION
import ftl.environment.TAGS
import ftl.environment.TestEnvironmentInfo
import ftl.environment.VIRTUAL_DEVICE
import ftl.environment.createTableColumnFor
import ftl.environment.getOrCreateList
import ftl.environment.orUnknown
import ftl.environment.tagToSystemOutColorMapper
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
    when (it) {
        PHYSICAL_DEVICE -> SystemOutColor.YELLOW
        VIRTUAL_DEVICE -> SystemOutColor.BLUE
        EMULATOR_DEVICE -> SystemOutColor.GREEN
        else -> SystemOutColor.DEFAULT
    }
}
