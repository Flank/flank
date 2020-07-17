package ftl.environment

import com.google.api.services.testing.model.AndroidVersion
import com.google.api.services.testing.model.Date
import ftl.reports.api.twoDigitString
import ftl.util.applyColorsUsing
import ftl.util.buildTable

fun List<AndroidVersion>.asPrintableTable() = createTestEnvironmentInfo().createAndroidSoftwareVersionsTable()

private fun List<AndroidVersion>.createTestEnvironmentInfo() =
    fold(mutableMapOf<String, MutableList<String>>()) { softwareInfo, softwareVersion ->
        softwareInfo.apply {
            getOrCreateList(OS_VERSION_ID).add(softwareVersion.id.orUnknown())
            getOrCreateList(VERSION).add(softwareVersion.versionString.orUnknown())
            getOrCreateList(CODE_NAME).add(softwareVersion.codeName.orUnknown())
            getOrCreateList(API_LEVEL).add(softwareVersion.apiLevel?.toString().orUnknown())
            getOrCreateList(RELEASE_DATE).add(softwareVersion.releaseDate.printableReleaseDate())
            getOrCreateList(TAGS).add(softwareVersion.tags.orEmpty().joinToString())
        }
    }

private fun Date?.printableReleaseDate() =
    if (this == null || year == null || month == null || day == null) "UNKNOWN"
    else "$year-${month.twoDigitString()}-${day.twoDigitString()}"

private fun TestEnvironmentInfo.createAndroidSoftwareVersionsTable() = buildTable(
    createTableColumnFor(OS_VERSION_ID),
    createTableColumnFor(VERSION),
    createTableColumnFor(CODE_NAME),
    createTableColumnFor(API_LEVEL),
    createTableColumnFor(RELEASE_DATE),
    createTableColumnFor(TAGS).applyColorsUsing(tagToSystemOutColorMapper)
)

const val VERSION = "VERSION"
const val CODE_NAME = "CODE_NAME"
const val API_LEVEL = "API_LEVEL"
const val RELEASE_DATE = "RELEASE_DATE"
