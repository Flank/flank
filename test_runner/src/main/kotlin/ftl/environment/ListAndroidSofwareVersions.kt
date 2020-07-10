package ftl.environment

import com.google.api.services.testing.model.AndroidVersion
import ftl.reports.api.twoDigitString
import ftl.util.applyColorsUsing
import ftl.util.buildTable

fun List<AndroidVersion>.asPrintableTable() = createTestEnvironmentInfo().createAndroidSoftwareVersionsTable()

private fun List<AndroidVersion>.createTestEnvironmentInfo() =
    fold(mutableMapOf<String, MutableList<String>>()) { softwareInfo, softwareVersion ->
        softwareInfo.apply {
            getOrCreateList(OS_VERSION_ID).add(softwareVersion.id)
            getOrCreateList(VERSION).add(softwareVersion.versionString)
            getOrCreateList(CODE_NAME).add(softwareVersion.codeName)
            getOrCreateList(API_LEVEL).add(softwareVersion.apiLevel.toString())
            getOrCreateList(RELEASE_DATE).add(with(softwareVersion.releaseDate) { "$year-${month.twoDigitString()}-${day.twoDigitString()}" })
            getOrCreateList(TAGS).add(softwareVersion.tags.orEmpty().joinToString())
        }
    }

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
