package ftl.adapter.google

import ftl.api.Locale

internal fun List<com.google.testing.model.Locale>.toApiModel(): List<Locale> = map { locale ->
    Locale(
        id = locale.id,
        name = locale.name,
        region = locale.region,
        tags = locale.tags
    )
}
