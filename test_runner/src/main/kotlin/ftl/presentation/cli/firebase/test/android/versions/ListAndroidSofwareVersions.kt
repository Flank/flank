package ftl.presentation.cli.firebase.test.android.versions

import ftl.api.OsVersion
import ftl.environment.OS_VERSION_ID
import ftl.environment.TAGS
import ftl.environment.TestEnvironmentInfo
import ftl.environment.createTableColumnFor
import ftl.environment.getOrCreateList
import ftl.environment.orUnknown
import ftl.environment.tagToSystemOutColorMapper
import ftl.reports.api.twoDigitString
import ftl.util.Alignment
import ftl.util.applyColorsUsing
import ftl.util.buildTable

fun List<OsVersion.Android>.toCliTable() = createTestEnvironmentInfo().createAndroidSoftwareVersionsTable()

private fun List<OsVersion.Android>.createTestEnvironmentInfo() =
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

private fun OsVersion.Date?.printableReleaseDate() =
    if (this == null || year == null || month == null || day == null) "UNKNOWN"
    else "$year-${month.twoDigitString()}-${day.twoDigitString()}"

private fun TestEnvironmentInfo.createAndroidSoftwareVersionsTable() = buildTable(
    createTableColumnFor(OS_VERSION_ID, Alignment.CENTER),
    createTableColumnFor(VERSION, Alignment.CENTER),
    createTableColumnFor(CODE_NAME, Alignment.LEFT),
    createTableColumnFor(API_LEVEL, Alignment.CENTER),
    createTableColumnFor(RELEASE_DATE, Alignment.CENTER),
    createTableColumnFor(TAGS, Alignment.CENTER).applyColorsUsing(tagToSystemOutColorMapper)
)

const val VERSION = "VERSION"
const val CODE_NAME = "CODE_NAME"
const val API_LEVEL = "API_LEVEL"
const val RELEASE_DATE = "RELEASE_DATE"
