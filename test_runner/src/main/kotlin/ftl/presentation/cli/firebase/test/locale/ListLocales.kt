package ftl.presentation.cli.firebase.test.locale

import ftl.api.Locale
import ftl.environment.TAGS
import ftl.environment.TestEnvironmentInfo
import ftl.environment.createTableColumnFor
import ftl.environment.getOrCreateList
import ftl.environment.tagToSystemOutColorMapper
import ftl.run.exception.FlankGeneralError
import ftl.util.Alignment
import ftl.util.applyColorsUsing
import ftl.util.buildTable

fun List<Locale>.toCliTable() = createTestEnvironment().createLocalesTable()

fun List<Locale>.createTestEnvironment() =
    fold(mutableMapOf<String, MutableList<String>>()) { allLocales, locale ->
        allLocales.apply {
            getOrCreateList(LOCALE).add(locale.id.orEmpty())
            getOrCreateList(NAME).add(locale.name.orEmpty())
            getOrCreateList(REGION).add(locale.region.orEmpty())
            getOrCreateList(TAGS).add(locale.tags.joinToString().orEmpty())
        }
    }

fun TestEnvironmentInfo.createLocalesTable() = buildTable(
    createTableColumnFor(LOCALE, Alignment.LEFT),
    createTableColumnFor(NAME, Alignment.LEFT),
    createTableColumnFor(REGION, Alignment.LEFT),
    createTableColumnFor(TAGS, Alignment.CENTER).applyColorsUsing(tagToSystemOutColorMapper)
)

private const val LOCALE = "LOCALE"
private const val NAME = "NAME"
private const val REGION = "REGION"

fun List<Locale>.getLocaleDescription(localeId: String) =
    findLocales(localeId)?.prepareDescription().orErrorMessage(localeId).plus("\n")

private fun List<Locale>.findLocales(localeId: String) = find { it.id == localeId }

fun Locale.prepareDescription() = """
id: $id
name: $name
""".trimIndent().addRegionIfExist(region).addTagsIfExists(this)

private fun String.addRegionIfExist(region: String?) =
    if (!region.isNullOrEmpty()) StringBuilder(this).appendLine("\nregion: $region").trim().toString()
    else this

private fun String.addTagsIfExists(locale: Locale) =
    if (!locale.tags.isNullOrEmpty()) StringBuilder(this).appendLine("\ntags:").appendTagsToList(locale)
    else this

private fun StringBuilder.appendTagsToList(locale: Locale) = apply {
    locale.tags.filterNotNull().forEach { tag -> appendLine("- $tag") }
}.trim().toString()

private fun String?.orErrorMessage(locale: String) =
    this ?: throw FlankGeneralError("ERROR: '$locale' is not a valid locale")
