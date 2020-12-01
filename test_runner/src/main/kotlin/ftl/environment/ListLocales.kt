package ftl.environment

import com.google.testing.model.Locale
import ftl.util.applyColorsUsing
import ftl.util.buildTable

fun List<Locale>.asPrintableTable() = createTestEnvironment().createLocalesTable()

private fun List<Locale>.createTestEnvironment() =
    fold(mutableMapOf<String, MutableList<String>>()) { allLocales, locale ->
        allLocales.apply {
            getOrCreateList(LOCALE).add(locale.id.orEmpty())
            getOrCreateList(NAME).add(locale.name.orEmpty())
            getOrCreateList(REGION).add(locale.region.orEmpty())
            getOrCreateList(TAGS).add(locale.tags?.joinToString().orEmpty())
        }
    }

private fun TestEnvironmentInfo.createLocalesTable() = buildTable(
    createTableColumnFor(LOCALE),
    createTableColumnFor(NAME),
    createTableColumnFor(REGION),
    createTableColumnFor(TAGS).applyColorsUsing(tagToSystemOutColorMapper)
)

private const val LOCALE = "LOCALE"
private const val NAME = "NAME"
private const val REGION = "REGION"
