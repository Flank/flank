package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.Locale
import ftl.api.Platform
import ftl.client.google.AndroidCatalog
import ftl.client.google.IosCatalog

object GoogleLocalesFetch :
    Locale.Fetch,
    (Locale.Identity) -> List<Locale> by { id ->
        when (id.platform) {
            Platform.ANDROID -> AndroidCatalog.getLocales(id.projectId).toApiModel()
            Platform.IOS -> IosCatalog.getLocales(id.projectId).toApiModel()
        }
    }
