package ftl.environment

import com.google.api.services.testing.model.Locale

fun List<Locale>.getLocaleDescription(localeId: String) = findLocales(localeId)?.prepareDescription().orErrorMessage(localeId)

private fun List<Locale>.findLocales(localeId: String) = find { it.id == localeId }

private fun Locale.prepareDescription() = """
    id: $id
    name: $name
""".trimIndent().addRegionIfExist(region).addTagsIfExists(this)

private fun String.addRegionIfExist(region: String?) =
    if (!region.isNullOrEmpty()) StringBuilder(this).appendln("\nregion: $region").toString()
    else this

private fun String.addTagsIfExists(locale: Locale) =
    if (!locale.tags.isNullOrEmpty()) StringBuilder(this).appendln("\ntags:").appendTagsToList(locale)
    else this

private fun StringBuilder.appendTagsToList(locale: Locale) = apply {
    locale.tags.filterNotNull().forEach { tag -> appendln("- $tag") }
}.toString().trim()

private fun String?.orErrorMessage(locale: String) = this ?: "ERROR: '$locale' is not a valid locale"
