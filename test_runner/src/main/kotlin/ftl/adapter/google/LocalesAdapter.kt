package ftl.adapter.google

import ftl.api.Locale
import com.google.testing.model.Locale as GoogleApiLocale

internal fun List<GoogleApiLocale>.toApiModel(): List<Locale> = map { locale ->
    Locale(
        id = locale.id.orEmpty(),
        name = locale.name.orEmpty(),
        region = locale.region.orEmpty(),
        tags = locale.tags.orEmpty()
    )
}
