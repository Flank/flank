package ftl.environment

import com.google.api.services.testing.model.Locale
import ftl.run.exception.FlankGeneralError

fun List<Locale>.getLocaleDescription(localeId: String) = findLocales(localeId)?.prepareDescription().orErrorMessage(localeId).plus("\n")

private fun List<Locale>.findLocales(localeId: String) = find { it.id == localeId }

private fun Locale.prepareDescription() = """
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

private fun String?.orErrorMessage(locale: String) = this ?: throw FlankGeneralError("ERROR: '$locale' is not a valid locale")
