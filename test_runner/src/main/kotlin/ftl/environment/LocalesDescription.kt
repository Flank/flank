package ftl.environment

import com.google.api.services.testing.model.Locale

fun List<Locale>.getLocaleDescription(locale: String) = findLocales(locale)?.prepareDescription().orErrorMessage(locale)

private fun List<Locale>.findLocales(locale: String) = find { it.id == locale }

private fun Locale.prepareDescription() = """
    id: $id
    name: $name
""".trimIndent()

private fun String?.orErrorMessage(locale: String) = this ?: "ERROR: $locale is not a valid locale".trimIndent()
