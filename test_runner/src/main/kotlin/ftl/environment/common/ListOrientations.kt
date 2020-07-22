package ftl.environment.common

import com.google.api.services.testing.model.Orientation
import ftl.environment.TestEnvironmentInfo
import ftl.environment.createTableColumnFor
import ftl.environment.getOrCreateList
import ftl.environment.tagToSystemOutColorMapper
import ftl.util.applyColorsUsing
import ftl.util.buildTable

fun List<Orientation>.asPrintableTable() = createOrientationsDetails().createOrientationsTable()

private fun List<Orientation>.createOrientationsDetails() = fold(mutableMapOf<String, MutableList<String>>()) { orientationInfo, orientation ->
    orientationInfo.apply {
        getOrCreateList(ORIENTATION_ID).add(orientation.id.orEmpty())
        getOrCreateList(NAME).add(orientation.name.orEmpty())
        getOrCreateList(TAG).add(orientation.tags?.joinToString(TAG_SEPARATOR).orEmpty())
    }
}

private fun TestEnvironmentInfo.createOrientationsTable() = buildTable(
    createTableColumnFor(ORIENTATION_ID),
    createTableColumnFor(NAME),
    createTableColumnFor(TAG).applyColorsUsing(tagToSystemOutColorMapper)
)

private const val ORIENTATION_ID = "ORIENTATION_ID"
private const val NAME = "NAME"
private const val TAG = "TAG"
private const val TAG_SEPARATOR = ","
