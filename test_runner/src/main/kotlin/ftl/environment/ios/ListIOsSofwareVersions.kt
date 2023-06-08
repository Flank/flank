package ftl.environment.ios

import com.google.api.services.testing.model.IosVersion
import ftl.api.OsVersion
import ftl.environment.OS_VERSION_ID
import ftl.environment.TAGS
import ftl.environment.TestEnvironmentInfo
import ftl.environment.createTableColumnFor
import ftl.environment.getOrCreateList
import ftl.environment.orUnknown
import ftl.environment.tagToSystemOutColorMapper
import ftl.util.Align
import ftl.util.applyColorsUsing
import ftl.util.buildTable

fun OsVersion.Ios.Available.toCliTable() = list.toCliTable()

fun List<OsVersion.Ios>.toCliTable() = createTestEnvironmentInfo().createIOsSoftwareVersionsTable()

private fun List<OsVersion.Ios>.createTestEnvironmentInfo() =
    fold(mutableMapOf<String, MutableList<String>>()) { softwareInfo, softwareVersion ->
        softwareInfo.apply {
            getOrCreateList(OS_VERSION_ID).add(softwareVersion.id.orUnknown())
            getOrCreateList(MAJOR_VERSION).add(softwareVersion.majorVersion?.toString().orUnknown())
            getOrCreateList(MINOR_VERSION).add(softwareVersion.minorVersion?.toString().orUnknown())
            getOrCreateList(TAGS).add(softwareVersion.tags.orEmpty().joinToString())
            getOrCreateList(SUPPORTED_XCODE_VERSION_IDS).add(softwareVersion.supportedXcodeVersionIds.orEmpty().joinToString())
        }
    }

// todo code duplicated for backward compatibility -- will be removed in one of future refactor tasks
fun List<IosVersion>.iosVersionsToCliTable() = createTestEnvironmentInfoFromIosVersions().createIOsSoftwareVersionsTable()

private fun List<IosVersion>.createTestEnvironmentInfoFromIosVersions() =
    fold(mutableMapOf<String, MutableList<String>>()) { softwareInfo, softwareVersion ->
        softwareInfo.apply {
            getOrCreateList(OS_VERSION_ID).add(softwareVersion.id.orUnknown())
            getOrCreateList(MAJOR_VERSION).add(softwareVersion.majorVersion?.toString().orEmpty())
            getOrCreateList(MINOR_VERSION).add(softwareVersion.minorVersion?.toString().orEmpty())
            getOrCreateList(TAGS).add(softwareVersion.tags.orEmpty().joinToString())
            getOrCreateList(SUPPORTED_XCODE_VERSION_IDS).add(softwareVersion.supportedXcodeVersionIds.joinToString())
        }
    }

private fun TestEnvironmentInfo.createIOsSoftwareVersionsTable() = buildTable(
    createTableColumnFor(OS_VERSION_ID, Align.CENTER),
    createTableColumnFor(MAJOR_VERSION, Align.CENTER),
    createTableColumnFor(MINOR_VERSION, Align.CENTER),
    createTableColumnFor(TAGS, Align.CENTER).applyColorsUsing(tagToSystemOutColorMapper),
    createTableColumnFor(SUPPORTED_XCODE_VERSION_IDS)
)

private const val MAJOR_VERSION = "MAJOR_VERSION"
private const val MINOR_VERSION = "MINOR_VERSION"
private const val SUPPORTED_XCODE_VERSION_IDS = "SUPPORTED_XCODE_VERSION_IDS"
