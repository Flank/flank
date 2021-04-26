package ftl.adapter.google

import ftl.api.Locale

internal fun List<com.google.testing.model.Locale>.toApiModel(): List<Locale> = map { locale ->
    Locale(
        id = locale.id.orEmpty(),
        name = locale.name.orEmpty(),
        region = locale.region.orEmpty(),
        tags = locale.tags.orEmpty()
    )
}
