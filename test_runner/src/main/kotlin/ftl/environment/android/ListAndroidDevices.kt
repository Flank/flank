package ftl.environment.android

import ftl.api.DeviceModel
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
import ftl.environment.isValid
import ftl.environment.tagToSystemOutColorMapper
import ftl.util.Align
import ftl.util.SystemOutColor
import ftl.util.alignToTheXMark
import ftl.util.applyColorsUsing
import ftl.util.buildTable

fun DeviceModel.Android.Available.toCliTable() = list.toCliTable()

fun List<DeviceModel.Android>.toCliTable() = createTestEnvironmentInfo().createAndroidDevicesTable()

private fun List<DeviceModel.Android>.createTestEnvironmentInfo() =
    fold(mutableMapOf<String, MutableList<String>>()) { devicesInfo, androidDevice ->
        devicesInfo.apply {
            getOrCreateList(MODEL_ID).add(androidDevice.codename)
            getOrCreateList(MAKE).add(androidDevice.manufacturer)
            getOrCreateList(MODEL_NAME).add(androidDevice.name)
            getOrCreateList(FORM).add(androidDevice.form)
            getOrCreateList(RESOLUTION).add(androidDevice.resolution)
            getOrCreateList(OS_VERSION_IDS).add(androidDevice.supportedVersionIds.joinToString())
            getOrCreateList(TAGS).add(androidDevice.tags.joinToString())
        }
    }

private fun TestEnvironmentInfo.createAndroidDevicesTable() = buildTable(
    createTableColumnFor(MODEL_ID),
    createTableColumnFor(MAKE),
    createTableColumnFor(MODEL_NAME),
    createTableColumnFor(FORM).applyColorsUsing(formToSystemOutColorMapper),
    createTableColumnFor(RESOLUTION, Align.CENTER).alignToTheXMark(),
    createTableColumnFor(OS_VERSION_IDS),
    createTableColumnFor(TAGS).applyColorsUsing(tagToSystemOutColorMapper)
)

private val DeviceModel.Android.resolution
    get() = if (screenX.isValid() && screenY.isValid()) "$screenY x $screenX" else "UNKNOWN"

private val formToSystemOutColorMapper: (String) -> SystemOutColor = {
    when (it) {
        PHYSICAL_DEVICE -> SystemOutColor.YELLOW
        VIRTUAL_DEVICE -> SystemOutColor.BLUE
        EMULATOR_DEVICE -> SystemOutColor.GREEN
        else -> SystemOutColor.DEFAULT
    }
}
