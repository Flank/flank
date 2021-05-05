package ftl.environment

import ftl.api.TestEnvironment
import ftl.environment.android.toCliTable
import ftl.environment.common.toCliTable
import ftl.util.SystemOutColor
import ftl.util.TableColumn

typealias TestEnvironmentInfo = MutableMap<String, MutableList<String>>

internal fun TestEnvironmentInfo.getOrCreateList(key: String) = getOrPut(key) { mutableListOf() }

internal fun TestEnvironmentInfo.createTableColumnFor(key: String) = TableColumn(key, getValue(key))

internal val tagToSystemOutColorMapper: (String) -> SystemOutColor = {
    when {
        it.contains("deprecated=") -> SystemOutColor.RED
        it == "default" -> SystemOutColor.GREEN
        it == "beta" -> SystemOutColor.YELLOW
        else -> SystemOutColor.DEFAULT
    }
}

internal fun String?.orUnknown() = this ?: "UNKNOWN"
internal fun Int?.orUnspecified() = this ?: UNSPECIFIED
internal fun Int.isValid() = this != UNSPECIFIED

private const val UNSPECIFIED = Integer.MIN_VALUE

const val MODEL_ID = "MODEL_ID"
const val MAKE = "MAKE"
const val MODEL_NAME = "MODEL_NAME"
const val FORM = "FORM"
const val RESOLUTION = "RESOLUTION"
const val OS_VERSION_IDS = "OS_VERSION_IDS"
const val TAGS = "TAGS"
const val PHYSICAL_DEVICE = "PHYSICAL"
const val VIRTUAL_DEVICE = "VIRTUAL"
const val EMULATOR_DEVICE = "EMULATOR"
const val OS_VERSION_ID = "OS_VERSION_ID"

// todo move somewhere else?
fun TestEnvironment.Android.prepareOutputString() = buildString {
    appendLine(osVersions.toCliTable())
    appendLine(models.toCliTable())
    appendLine(locales.toCliTable())
    appendLine(softwareCatalog.toCliTable())
    appendLine(networkProfiles.toCliTable())
    appendLine(orientations.toCliTable())
    appendLine(ipBlocks.toCliTable())
}
