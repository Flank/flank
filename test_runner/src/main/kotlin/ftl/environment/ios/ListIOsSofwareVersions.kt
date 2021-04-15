package ftl.environment.ios

import com.google.testing.model.IosVersion
import ftl.adapter.environment.OS_VERSION_ID
import ftl.adapter.environment.TAGS
import ftl.adapter.environment.TestEnvironmentInfo
import ftl.adapter.environment.createTableColumnFor
import ftl.adapter.environment.getOrCreateList
import ftl.adapter.environment.orUnknown
import ftl.adapter.environment.tagToSystemOutColorMapper
import ftl.util.applyColorsUsing
import ftl.util.buildTable

fun List<IosVersion>.asPrintableTable() = createTestEnvironmentInfo().createIOsSoftwareVersionsTable()

private fun List<IosVersion>.createTestEnvironmentInfo() =
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
    createTableColumnFor(OS_VERSION_ID),
    createTableColumnFor(MAJOR_VERSION),
    createTableColumnFor(MINOR_VERSION),
    createTableColumnFor(TAGS).applyColorsUsing(tagToSystemOutColorMapper),
    createTableColumnFor(SUPPORTED_XCODE_VERSION_IDS)
)

private const val MAJOR_VERSION = "MAJOR_VERSION"
private const val MINOR_VERSION = "MINOR_VERSION"
private const val SUPPORTED_XCODE_VERSION_IDS = "SUPPORTED_XCODE_VERSION_IDS"
